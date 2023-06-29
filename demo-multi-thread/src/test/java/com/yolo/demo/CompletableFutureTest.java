package com.yolo.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Collectors;

@Slf4j
public class CompletableFutureTest extends DemoMultiThreadApplicationTests{


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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Data:"+ id;
    }
    private  String sendData(String data) {
        System.out.println("sendData: "+ Thread.currentThread().getName());
        System.out.println(data);
        return data;
    }

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

    @Test
    @SneakyThrows
    public void thenAccept(){
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> new Random().nextInt(10)).thenAccept(integer -> {
            System.out.println(integer);
        });
        future.get();
    }

    @Test
    @SneakyThrows
    public void thenRun(){
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> new Random().nextInt(10)).thenRun(() -> {
            System.out.println("thenRun ...");
        });
        future.get();
    }

    @Test
    @SneakyThrows
    public void thenCombine(){
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "hello1");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "hello2");
        CompletableFuture<String> result = future1.thenCombine(future2, (t, u) -> t + "-" + u);
        System.out.println(result.get());
    }

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

    @Test
    @SneakyThrows
    public void join(){
        List<Integer> list = Arrays.asList(10,20,30,40);
        List<Integer> collect = list.stream().map(data -> CompletableFuture.supplyAsync(() -> getNumber(data))).map(CompletableFuture::join).collect(Collectors.toList());
    }

    private static int getNumber(int a){
        return a*a;
    }



}
