package com.weibo.transform;

import com.weibo.pojo.Event;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.stream.Collector;

/**
 * 基本转换
 */
public class TransTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> stream = env.fromElements(
                new Event("marry", "/home", 1000L),
                new Event("jack", "/school", 2000L),
                new Event("bob", "/car", 6000L),
                new Event("chen", "/school", 3000L)
        );
        //1.Map转换
        //1.1 匿名内部类，实现MapFunction，泛型类型分别表示输入和输出类型。
        /*stream.map(new MapFunction<Event, String>() {
            @Override
            public String map(Event value) throws Exception {
                return value.user;
            }
        }).print();*/

        //1.2 MapFunction实现类
        stream.map(new UserExtractor()).print("map");

        //2.Filter
        //2.1 匿名内部类，实现FilterFunction接口
        stream.filter((FilterFunction<Event>) value -> "/school".equals(value.url)).print("filter");

        /*stream.filter(new FilterFunction<Event>() {
            @Override
            public boolean filter(Event value) throws Exception {
                return "/school".equals(value.url);
            }
        }).print("filter");
        */
        //2.2 FilterFunction实现类
        stream.filter(new UserFilter()).print("filter2");

        //flatMap
        stream.flatMap((FlatMapFunction<Event, String>) (value, out) -> {
            if (value.user.equals("marry")) {
                out.collect(value.user);
            } else if (value.user.equals("bob")) {
                out.collect(value.user);
                out.collect(value.url);
            }
        }).print("flatMap");
        /*
        stream.flatMap(new FlatMapFunction<Event, String>() {
            @Override
            public void flatMap(Event value, org.apache.flink.util.Collector<String> out) throws Exception {
                if (value.user.equals("marry")) {
                    out.collect(value.user);
                } else if (value.user.equals("bob")) {
                    out.collect(value.user);
                    out.collect(value.url);
                }
            }
        }).print("flatMap");
        */

        env.execute();


    }

    public static class UserExtractor implements MapFunction<Event, String> {
        @Override
        public String map(Event value) throws Exception {
            return value.user;
        }
    }

    public static class UserFilter implements FilterFunction<Event> {
        @Override
        public boolean filter(Event value) throws Exception {
            return "bob".equals(value.user);
        }
    }

}
