package com.caique.todolist.utils;

import java.util.Arrays;

import org.springframework.beans.BeanUtils;

public class Utils {
  public static String[] getNullPropertyNames(Object source) {
    return Arrays.stream(BeanUtils.getPropertyDescriptors(source.getClass()))
        .map(propertyDescriptor -> propertyDescriptor.getName())
        .filter(propertyName -> {
          try {
            var readMethod = BeanUtils.getPropertyDescriptor(source.getClass(), propertyName).getReadMethod();
            if (readMethod == null) {
              return false;
            }
            Object value = readMethod.invoke(source);
            return value == null;
          } catch (Exception e) {
            return false;
          }
        })
        .toArray(String[]::new);
  }
}
