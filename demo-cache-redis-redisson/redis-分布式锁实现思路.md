# redis-分布式锁实现思路

## 一、介绍

### 1、什么是分布式锁

分布式锁，即分布式系统中的锁。在单体应用中我们通过锁解决的是**控制共享资源访问**的问题，而分布式锁，就是解决了**分布式系统中控制共享资源访问**的问题。与单体应用不同的是，分布式系统中竞争共享资源的最小粒度从线程升级成了进程

### 2、分布式锁需要具备的条件

- 在分布式系统环境下，一个方法在同一时间只能被一个机器的一个线程执行
- 高可用的获取锁与释放锁
- 高性能的获取锁与释放锁
- 具备可重入特性（可理解为重新进入，由多于一个任务并发使用，而不必担心数据错误）
- 具备锁失效机制，即自动解锁，防止死锁
- 具备非阻塞锁特性，即没有获取到锁将直接返回获取锁失败

### 3、实现方式

- 基于数据库实现分布式锁
- 基于Zookeeper实现分布式锁
- 基于reids实现分布式锁

## 二、基于redis实现分布式锁

### 命令介绍

```java
(1) SETNX key value
  当且仅当 key 不存在时，将 key 的值设为 value。若给定的 key 已经存在，则 SETNX 不做任何动作
  设置成功，返回 1 
  设置失败，返回 0

(2) DEL key [key ...]  
   删除给定的一个或多个 key ,不存在的 key 会被忽略
  
(3) EXPIRE key seconds
  为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除(时间单位为秒)

(4) EXPIREAT key timestamp 
  EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间,不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)
  
(5) SET key value [EX seconds] [PX milliseconds] [NX|XX]
  EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key second value
  PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX millisecond 效果等同于 PSETEX key millisecond value
  NX ：只在键不存在时，才对键进行设置操作。 SET key value NX 效果等同于 SETNX key value
  XX ：只在键已经存在时，才对键进行设置操作
  
(6) GET key
  返回 key 所关联的字符串值,如果 key 不存在那么返回特殊值 nil
  
(7) GETSET key value
  该方法是原子的，对key设置newValue这个值，并且返回key原来的旧值
```

[redis 命令参数](http://doc.redisfans.com/index.html)

### 方案一：基于set命令的分布式锁

1、加锁：使用setnx进行加锁，当该指令返回1时，说明成功获得锁

2、解锁：当得到锁的线程执行完任务之后，使用del命令释放锁，以便其他线程可以继续执行setnx命令来获得锁

> （1）存在的问题：假设线程获取了锁之后，在执行任务的过程中挂掉，来不及显示地执行del命令释放锁，那么竞争该锁的线程都会执行不了，产生死锁的情况。
>
> （2）解决方案：设置锁超时时间

3、设置锁超时时间：setnx 的 key 必须设置一个超时时间，以保证即使没有被显式释放，这把锁也要在一定时间后自动释放。可以使用expire命令设置锁超时时间

> （1）存在问题：setnx 和 expire 不是原子性的操作，假设某个线程执行setnx 命令，成功获得了锁，但是还没来得及执行expire 命令，服务器就挂掉了，这样一来，这把锁就没有设置过期时间了，变成了死锁，别的线程再也没有办法获得锁了。
>
> （2）解决方案：redis的set命令支持在获取锁的同时设置key的过期时间

4 、<font color = 'red'>使用set命令加锁并设置锁过期时间</font>

> （1）存在问题：
>
> ​		① 假如线程A成功得到了锁，并且设置的超时时间是 30 秒。如果某些原因导致线程 A 执行的很慢，过了 30 秒都没执行完，这时候锁过期自动释放，线程 B 得到了锁。
>
> ​		② 随后，线程A执行完任务，接着执行del指令来释放锁。但这时候线程 B 还没执行完，线程A实际上删除的是线程B加的锁。
>
> （2）解决方案：
>
> 可以在 del 释放锁之前做一个判断，验证当前的锁是不是自己加的锁。在加锁的时候把当前的线程 ID 当做value，并在删除之前验证 key 对应的 value 是不是自己线程的 ID。但是，这样做其实隐含了一个新的问题，get操作、判断和释放锁是两个独立操作，不是原子性。对于非原子性的问题，我们可以使用Lua脚本来确保操作的原子性

5、锁续期

虽然步骤4避免了线程A误删掉key的情况，但是同一时间有 A，B 两个线程在访问代码块，仍然是不完美的。怎么办呢？我们可以让获得锁的线程开启一个**守护线程**，用来给快要过期的锁“续期”

> ① 假设线程A执行了29 秒后还没执行完，这时候守护线程会执行 expire 指令，为这把锁续期 20 秒。守护线程从第 29 秒开始执行，每 20 秒执行一次。
>
> ② 情况一：当线程A执行完任务，会显式关掉守护线程。
>
> ③ 情况二：如果服务器忽然断电，由于线程 A 和守护线程在同一个进程，守护线程也会停下。这把锁到了超时的时候，没人给它续命，也就自动释放了。

### 方案二：基于setnx、get、getset的分布式锁

1、<font color = 'red'>实现原理</font>

> （1）setnx(lockkey, 当前时间+过期超时时间) ，如果返回1，则获取锁成功；如果返回0则没有获取到锁，转向步骤(2)
>
> （2）get(lockkey)获取值oldExpireTime ，并将这个value值与当前的系统时间进行比较，如果小于当前系统时间，则认为这个锁已经超时，可以允许别的请求重新获取，转向步骤(3)
>
> （3）计算新的过期时间 newExpireTime=当前时间+锁超时时间，然后getset(lockkey, newExpireTime) 会返回当前lockkey的值currentExpireTime
>
> （4）判断 currentExpireTime 与 oldExpireTime 是否相等，如果相等，说明当前getset设置成功，获取到了锁。如果不相等，说明这个锁又被别的请求获取走了，那么当前请求可以直接返回失败，或者继续重试。
>
> （5）在获取到锁之后，当前线程可以开始自己的业务处理，当处理完毕后，比较自己的处理时间和对于锁设置的超时时间，如果小于锁设置的超时时间，则直接执行del命令释放锁（释放锁之前需要判断持有锁的线程是不是当前线程）；如果大于锁设置的超时时间，则不需要再锁进行处理。

2、代码实现

```java
public boolean lock(long acquireTimeout, TimeUnit timeUnit) throws InterruptedException {
    acquireTimeout = timeUnit.toMillis(acquireTimeout);
    long acquireTime = acquireTimeout + System.currentTimeMillis();
    //使用J.U.C的ReentrantLock
    threadLock.tryLock(acquireTimeout, timeUnit);
    try {
    	//循环尝试
        while (true) {
        	//调用tryLock
            boolean hasLock = tryLock();
            if (hasLock) {
                //获取锁成功
                return true;
            } else if (acquireTime < System.currentTimeMillis()) {
                break;
            }
            Thread.sleep(sleepTime);
        }
    } finally {
        if (threadLock.isHeldByCurrentThread()) {
            threadLock.unlock();
        }
    }
 
    return false;
}
 
public boolean tryLock() {
 
    long currentTime = System.currentTimeMillis();
    String expires = String.valueOf(timeout + currentTime);
    //设置互斥量
    if (redisHelper.setNx(mutex, expires) > 0) {
    	//获取锁，设置超时时间
        setLockStatus(expires);
        return true;
    } else {
        String currentLockTime = redisUtil.get(mutex);
        //检查锁是否超时
        if (Objects.nonNull(currentLockTime) && Long.parseLong(currentLockTime) < currentTime) {
            //获取旧的锁时间并设置互斥量
            String oldLockTime = redisHelper.getSet(mutex, expires);
            //旧值与当前时间比较
            if (Objects.nonNull(oldLockTime) && Objects.equals(oldLockTime, currentLockTime)) {
            	//获取锁，设置超时时间
                setLockStatus(expires);
                return true;
            }
        }
 
        return false;
    }
}

public boolean unlock() {
    //只有锁的持有线程才能解锁
    if (lockHolder == Thread.currentThread()) {
        //判断锁是否超时，没有超时才将互斥量删除
        if (lockExpiresTime > System.currentTimeMillis()) {
            redisHelper.del(mutex);
            logger.info("删除互斥量[{}]", mutex);
        }
        lockHolder = null;
        logger.info("释放[{}]锁成功", mutex);
 
        return true;
    } else {
        throw new IllegalMonitorStateException("没有获取到锁的线程无法执行解锁操作");
    }
}
```

> 存在问题：
>
> （1）这个锁的核心是基于System.currentTimeMillis()，如果多台服务器时间不一致，那么问题就出现了，但是这个bug完全可以从服务器运维层面规避的，而且如果服务器时间不一样的话，只要和时间相关的逻辑都是会出问题的
>
> （2）如果前一个锁超时的时候，刚好有多台服务器去请求获取锁，那么就会出现同时执行redis.getset()而导致出现过期时间覆盖问题，不过这种情况并不会对正确结果造成影响
>
> （3）存在多个线程同时持有锁的情况：如果线程A执行任务的时间超过锁的过期时间，这时另一个线程就可以获得这个锁了，造成多个线程同时持有锁的情况。类似于方案一，可以使用“锁续期”的方式来解决。

### 前两种redis分布式锁的存在的问题

​	前面两种redis分布式锁的实现方式，如果从“高可用”的层面来看，仍然是有所欠缺，也就是说当 redis 是单点的情况下，当发生故障时，则整个业务的分布式锁都将无法使用。

​	为了提高可用性，我们可以使用主从模式或者哨兵模式，但在这种情况下仍然存在问题，在主从模式或者哨兵模式下，正常情况下，如果加锁成功了，那么master节点会异步复制给对应的slave节点。但是如果在这个过程中发生master节点宕机，主备切换，slave节点从变为了 master节点，而锁还没从旧master节点同步过来，这就发生了锁丢失，会导致多个客户端可以同时持有同一把锁的问题

### 方案三：基于Redisson看门狗的分布式锁

前面说了，如果某些原因导致持有锁的线程在锁过期时间内，还没执行完任务，而锁因为还没超时被自动释放了，那么就会导致多个线程同时持有锁的现象出现，而为了解决这个问题，可以进行“锁续期”。其实，在JAVA的Redisson包中有一个"看门狗"机制，已经帮我们实现了这个功能

**1、redisson原理：**

redisson在获取锁之后，会维护一个看门狗线程，当锁即将过期还没有释放时，不断的延长锁key的生存时间

![image-20230608163412344](https://gitee.com/huanglei1111/phone-md/raw/master/images/image-20230608163412344.png)

2、**watch dog自动延期机制**

> 看门狗启动后，对整体性能也会有一定影响，默认情况下看门狗线程是不启动的。如果使用redisson进行加锁的同时设置了锁的过期时间，也会导致看门狗机制失效。

edisson在获取锁之后，会维护一个看门狗线程，在每一个锁设置的过期时间的1/3处，如果线程还没执行完任务，则不断延长锁的有效期。看门狗的检查锁超时时间默认是30秒，可以通过 lockWactchdogTimeout 参数来改变

> 加锁的时间默认是30秒，如果加锁的业务没有执行完，那么每隔 30 ÷ 3 = 10秒，就会进行一次续期，把锁重置成30秒，保证解锁前锁不会自动失效

那万一业务的机器宕机了呢？如果宕机了，那看门狗线程就执行不了了，就续不了期，那自然30秒之后锁就解开了

3、**redisson分布式锁的关键点**

a. 对key不设置过期时间，由Redisson在加锁成功后给维护一个watchdog看门狗，watchdog负责定时监听并处理，在锁没有被释放且快要过期的时候自动对锁进行续期，保证解锁前锁不会自动失效

b. 通过Lua脚本实现了加锁和解锁的原子操作

c. 通过记录获取锁的客户端id，每次加锁时判断是否是当前客户端已经获得锁，实现了可重入锁。





