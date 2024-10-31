package org.gsstation.novin.core.common.configuration;

import org.gsstation.novin.core.exception.InvalidConfigurationException;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public interface ConfigurationUpdateListener<T> {
    void configurationUpdated(T context) throws InvalidConfigurationException;
}
