package net.lzzy.keeper.models;

import net.lzzy.sqllib.AsPrimaryKey;

import java.util.UUID;

/**
 * @author Administrator model=entity+业务逻辑
 */
class BaseEntity {
    @AsPrimaryKey
    protected UUID id;
    BaseEntity(){
        id=UUID.randomUUID();
    }

    public Object getIdentityValue() {
        return id;
    }

    public UUID getId() {
        return id;
    }
}
