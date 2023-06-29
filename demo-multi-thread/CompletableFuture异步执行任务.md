# CompletableFuture异步执行任务

### runAsync 和 supplyAsync方法

CompletableFuture 提供了四个静态方法来创建一个异步操作。

```java
public static CompletableFuture<Void> runAsync(Runnable runnable)
public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
```

没有指定Executor的方法会使用ForkJoinPool.commonPool() 作为它的线程池执行异步代码。如果指定线程池，则使用指定的线程池运行。以下所有的方法都类同。

- runAsync方法不支持返回值。
- supplyAsync可以支持返回值。

```java
    /**
     * 异步运行（无返回值）使用默认线程池 ForkJoinPool.commonPool()
     */
    @Test
    @SneakyThrows
    public void runAsync(){
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            System.out.println("run end ...");
        });

        Void unused = future.get();
        System.out.println(unused);
    }

    /**
     * 异步运行(有返回值) 使用默认线程池 ForkJoinPool.commonPool()
     */
    @Test
    @SneakyThrows
    public void supplyAsync(){
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
            System.out.println("run end ...");
            return System.currentTimeMillis();
        });

        Long aLong = future.get();
        System.out.println(aLong);
    }
```

### join方法

获取有返回值的最后结果的值

```java
    @Test
    @SneakyThrows
    public void join(){
        List<Integer> list = Arrays.asList(10,20,30,40);
        List<Integer> collect = list.stream().map(data -> CompletableFuture.supplyAsync(() -> getNumber(data))).map(CompletableFuture::join).collect(Collectors.toList());
    }

    private static int getNumber(int a){
        return a*a;
    }
```



### thenApply 方法

当一个线程依赖另一个线程时，可以使用 thenApply 方法来把这两个线程串行化

```java
public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn)
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn)
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor)
```

Function<? super T,? extends U>
T：上一个任务返回结果的类型
U：当前任务的返回值类型

```java
    @Test
    @SneakyThrows
    public void thenApply(){
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(()-> getDataById(10))
                .thenApply(this::sendData);

        String s = cf.get();
        System.out.println(s);
    }

    private  String getDataById(int id) {
        System.out.println("getDataById: "+ Thread.currentThread().getName());
        return "Data:"+ id;
    }
    private  String sendData(String data) {
        System.out.println("sendData: "+ Thread.currentThread().getName());
        System.out.println(data);
        return data;
    }
```

输出结果：

```java
getDataById: ForkJoinPool.commonPool-worker-1
sendData: main
Data:10 
```

解释：

主线程开始执行代码，当代码到达`supplyAsync()`时，`supplyAsync()`从`ForkJoinPool.commonPool()`获取新线程以异步执行其功能。

`thenApply()`将由主线程或`supplyAsync()`使用的线程执行

如果`supplyAsync()`的`Supplier`花费的时间更长，则`Apply()`将由`supplyAsync()`所使用的线程执行，因此主线程将不会被阻塞

要理解这一点，请按如下所示更改`getDataById()`方法

```java
    private  String getDataById(int id) {
        System.out.println("getDataById: "+ Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Data:"+ id;
    }
```

输出结果

```java
getDataById: ForkJoinPool.commonPool-worker-1
sendData: ForkJoinPool.commonPool-worker-1
Data:10
Data:10
```

### handle 方法

handle 是执行任务完成时对结果的处理。
handle 方法和 thenApply 方法处理方式基本一样。不同的是 handle 是在任务完成后再执行，还可以处理异常的任务。thenApply 只可以执行正常的任务，任务出现异常则不执行 thenApply 方法。

```java
public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);
public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);
public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void handle(){
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            int i= 10/0;
            return new Random().nextInt(10);
        }).handle((param, throwable) -> {
            int result = -1;
            if(throwable==null){
                result = param * 2;
            }else{
                System.out.println(throwable.getMessage());
            }
            return result;
        });
        System.out.println(future.get());
    }
```

输出结果

```java
java.lang.ArithmeticException: / by zero
-1
```

从示例中可以看出，在 handle 中可以根据任务是否有异常来进行做相应的后续处理操作。而 thenApply 方法，如果上个任务出现错误，则不会执行 thenApply 方法

### thenAccept 消费处理结果

接收任务的处理结果，并消费处理，无返回结果

```java
public CompletionStage<Void> thenAccept(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void thenAccept(){
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> new Random().nextInt(10)).thenAccept(integer -> {
            System.out.println(integer);
        });
        future.get();
    }
```

从示例代码中可以看出，该方法只是消费执行完成的任务，并可以根据上面的任务返回的结果进行处理。并没有后续的输错操作

### thenRun 方法

跟 thenAccept 方法不一样的是，不关心任务的处理结果。只要上面的任务执行完成，就开始执行 thenAccept

```java
public CompletionStage<Void> thenRun(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action);
public CompletionStage<Void> thenRunAsync(Runnable action,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void thenRun(){
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> new Random().nextInt(10)).thenRun(() -> {
            System.out.println("thenRun ...");
        });
        future.get();
    }
```

该方法同 thenAccept 方法类似。不同的是上个任务处理完成后，并不会把计算的结果传给 thenRun 方法。只是处理完任务后，执行 thenAccept 的后续操作。

### thenCombine 合并任务

thenCombine 会把 两个 CompletionStage 的任务都执行完成后，把两个任务的结果一块交给 thenCombine 来处理

```java
public <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void thenCombine(){
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "hello1");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "hello2");
        CompletableFuture<String> result = future1.thenCombine(future2, (t, u) -> t + "-" + u);
        System.out.println(result.get());
    }
```

### thenAcceptBoth 合并消费处理结果

当两个CompletionStage都执行完成后，把结果一块交给thenAcceptBoth来进行消耗

```java
public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action);
public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action);
public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action,     Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void thenAcceptBoth(){
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        });
        f1.thenAcceptBoth(f2, (t, u) -> System.out.println("f1="+t+";f2="+u+";"));
    }
```

### applyToEither 方法

两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的转化操作

```java
public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other,Function<? super T, U> fn);
public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,Function<? super T, U> fn);
public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,Function<? super T, U> fn,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void applyToEither(){
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        });
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        });

        CompletableFuture<Integer> result = f1.applyToEither(f2, t -> {
            System.out.println(t);
            return t * 2;
        });

        System.out.println(result.get());
    }
```

### acceptEither 方法

两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的消耗操作

```java
public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other,Consumer<? super T> action);
public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other,Consumer<? super T> action);
public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other,Consumer<? super T> action,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void acceptEither(){
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        });
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        });

        f1.acceptEither(f2, t -> System.out.println(t));
    }
```

### runAfterEither 方法

两个CompletionStage，任何一个完成了都会执行下一步的操作（Runnable）

```java
public CompletionStage<Void> runAfterEither(CompletionStage<?> other,Runnable action);
public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other,Runnable action);
public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other,Runnable action,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void runAfterEither(){
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        });
        f1.runAfterEither(f2, () -> System.out.println("上面有一个已经完成了。"));
    }
```

### runAfterBoth

两个CompletionStage，都完成了计算才会执行下一步的操作（Runnable）

```java
public CompletionStage<Void> runAfterBoth(CompletionStage<?> other,Runnable action);
public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other,Runnable action);
public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other,Runnable action,Executor executor);
```

```java
    @Test
    @SneakyThrows
    public void runAfterBoth(){
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        });

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        });
        f1.runAfterBoth(f2, () -> {
            Integer integer;
            Integer integer1;
            try {
                integer = f1.get();
                integer1 = f2.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            System.out.println("上面两个任务都执行完成了。" + integer + integer1);
        });
    }
```

### thenCompose 方法

thenCompose 方法允许你对两个 CompletionStage 进行流水线操作，第一个操作完成时，将其结果作为参数传递给第二个操作

```java
public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);
public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) ;
public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) ;
```

```java
    @Test
    @SneakyThrows

    public void thenCompose(){
        CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            System.out.println("t1="+t);
            return t;
        }).thenCompose(param -> CompletableFuture.supplyAsync(() -> {
            int t = param *2;
            System.out.println("t2="+t);
            return t;
        }));
        System.out.println("thenCompose result : "+f.get());
    }
```

