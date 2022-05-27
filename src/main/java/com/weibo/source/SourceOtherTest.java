package com.weibo.source;

import com.weibo.pojo.Event;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

/**
 * 从集合中读取
 */
public class SourceOtherTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        /**
         *

        //创建集合
        ArrayList<Event> arr = new ArrayList<>();
        arr.add(new Event("marry", "./home", 1000L));
        arr.add(new Event("bob", "./school", 3000L));
        arr.add(new Event("jack", "./car", 2000L));
         DataStreamSource<Event> stream = env.fromCollection(arr);
         stream.print("collectionScource");
         */
        /**
         *

        //文件读取
        DataStreamSource<String> stream = env.readTextFile("input/words.txt");
        stream.print("file");
         */
        //socket

        env.execute();
    }
}
