package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models;

import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.ByteTransformable;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.Validatable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by boot on 3/4/2018.
 */

public abstract class CommentOptionsExtension implements ByteTransformable, Validatable {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
