package cn.oneforce.javaagent;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description:
 * Author: HW
 * Date: 2023/8/8
 */
@Data
@AllArgsConstructor
public class Mytest<T> {
    private T value;
    private String name;
    private String code;
}
