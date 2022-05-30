package com.weibo.transform;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/*
所有的Flink函数类都有其Rich版本，
富函数类可以获取运行环境的上下文，并拥有一些生命周期方法，因此可以实现更复杂的功能。
典型的生命周期方法如：
open()、close()
注意：这里的生命周期方法对于一个并行子任务来说只会被调用一次。
 */
public class RichFunctionTest {

    // 常见应用场景：连接外部数据库进行读写操作。

    public class MyFlatMapFunction extends RichFlatMapFunction{

        @Override
        public void open(Configuration configuration) {
            //初始化工作，如建立JDBC连接。
        }


        @Override
        public void flatMap(Object value, Collector out) throws Exception {
            // 具体操作。
        }

        @Override
        public void close() throws Exception {
            // 清理工作，如关闭连接。
        }

        @Override
        public RuntimeContext getRuntimeContext() {

            return super.getRuntimeContext();
        }

    }
}
