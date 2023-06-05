package com.amazonaws.services.neptune.io;

import com.amazonaws.services.neptune.cli.AbstractTargetModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class KinesisConfigTest {

    private AbstractTargetModule target;

    @Before
    public void resetTargetModule() {
        target = spy(AbstractTargetModule.class);
    }

    @Test
    public void shouldCreateStreamIfNameAndRegionAreProvided() {
        when(target.getStreamName()).thenReturn("test");
        when(target.getRegion()).thenReturn("us-west-2");
        KinesisConfig config = new KinesisConfig(target);

        assertNotNull(config.stream());
    }

    @Test
    public void shouldNotCreateStreamIfNameNotProvided() {
        when(target.getStreamName()).thenReturn("");
        when(target.getRegion()).thenReturn("us-west-2");
        KinesisConfig config = new KinesisConfig(target);

        Throwable t = assertThrows(IllegalArgumentException.class, () -> config.stream());
        assertEquals("You must supply an AWS Region and Amazon Kinesis Data Stream name", t.getMessage());
    }

    @Test
    public void shouldNotCreateStreamIfRegionNotProvided() {
        when(target.getStreamName()).thenReturn("test");
        when(target.getRegion()).thenReturn("");
        KinesisConfig config = new KinesisConfig(target);

        Throwable t = assertThrows(IllegalArgumentException.class, () -> config.stream());
        assertEquals("You must supply an AWS Region and Amazon Kinesis Data Stream name", t.getMessage());
    }
}