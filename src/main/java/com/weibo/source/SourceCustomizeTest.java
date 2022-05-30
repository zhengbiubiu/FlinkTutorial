package com.weibo.source;

import com.weibo.pojo.Event;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Calendar;
import java.util.Random;

/**
 * 自定义数据源
 */
public class SourceCustomizeTest  {

    /**
     * SourceFunction接口定义的数据源并行度只能设置为1，如果设置大于1会报错
     */
    public static class CustomSource1 implements SourceFunction<Event>{
        // 声明一个布尔变量，作为控制数据生成的标识位
        private Boolean running = true;

        @Override
        public void run(SourceContext<Event> sourceContext) throws Exception {
            Random random = new Random();
            String[] users = {"marry", "jack", "bob"};
            String[] urls = {"/home", "/car", "school"};
            while (running) {
                sourceContext.collect(new Event(users[random.nextInt(users.length)],
                        urls[random.nextInt(urls.length)],
                        Calendar.getInstance().getTimeInMillis()));
                // 间隔1s
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }

    /**
     * 可以自定义并行度
     */
    static class CustomSource2 implements ParallelSourceFunction<Event>{
        // 声明一个布尔变量，作为控制数据生成的标识位
        private Boolean running = true;

        @Override
        public void run(SourceContext<Event> sourceContext) throws Exception {
            Random random = new Random();
            String[] users = {"marry", "jack", "bob"};
            String[] urls = {"/home", "/car", "school"};
            while (running) {
                sourceContext.collect(new Event(users[random.nextInt(users.length)],
                        urls[random.nextInt(urls.length)],
                        Calendar.getInstance().getTimeInMillis()));
                // 间隔1s
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        /*DataStreamSource<Event> stream1 = env.addSource(new CustomSource1()).setParallelism(1);
        //设置大于1会报错
        //DataStreamSource<Event> stream1 = env.addSource(new CustomSource1()).setParallelism(1);
        stream1.print("stream1");*/
        env.addSource(new CustomSource2()).setParallelism(1).print();
        env.execute();

    }


}
