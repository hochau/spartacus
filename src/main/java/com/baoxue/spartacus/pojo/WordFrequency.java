package com.baoxue.spartacus.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author C
 * @Date 2019/8/20 0:47
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordFrequency {
    String text;
    Integer weight;
}
