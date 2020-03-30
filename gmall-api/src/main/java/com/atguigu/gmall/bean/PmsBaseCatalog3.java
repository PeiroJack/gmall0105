package com.atguigu.gmall.bean;

import javax.persistence.Id;
import java.io.Serializable;

public class PmsBaseCatalog3 implements Serializable {

    @Id
    String id;
    String name;
    String catalog2Id;

}
