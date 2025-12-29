package com.wangchai.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

// 工具类：Bean 拷贝工具类
public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    // 方法：拷贝单个 bean 对象
    public static <V> V copyBean(Object source,Class<V> clazz){

       // 创建目标对象
        V target = null;
        try {

            target = clazz.newInstance();
            // 实现 复制属性
            BeanUtils.copyProperties(source, target);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回目标对象
        return target;

    }

    // 方法：拷贝多个 bean 对象
    public static <O,V> List<V> copyBeanList(List<O> list,Class<V> clazz){
        // 遍历 list 中的每个元素，进行 bean 拷贝
        return list.stream()
                .map(item -> copyBean(item, clazz))
                .collect(Collectors.toList());
    }

}
