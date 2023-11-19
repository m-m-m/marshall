/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package io.github.mmm.marshall.spi;

import java.io.IOException;

import io.github.mmm.base.exception.RuntimeIoException;
import io.github.mmm.marshall.EnumFormat;
import io.github.mmm.marshall.MarshallingConfig;
import io.github.mmm.marshall.StructuredFormat;
import io.github.mmm.marshall.StructuredProcessor;
import io.github.mmm.marshall.StructuredReader;
import io.github.mmm.marshall.StructuredState;
import io.github.mmm.marshall.StructuredWriter;
import io.github.mmm.marshall.id.StructuredIdMappingObject;

/**
 * Abstract base implementation of {@link StructuredReader}.
 *
 * @param <S> type of the {@link StructuredNode}.
 * @since 1.0.0
 */
public abstract class AbstractStructuredProcessor<S extends StructuredNode<S>> implements StructuredProcessor {

  private final StructuredFormat format;

  /** The {@link MarshallingConfig}. */
  protected final MarshallingConfig config;

  /** @see MarshallingConfig#VAR_ENUM_FORMAT */
  protected final EnumFormat enumFormat;

  /**
   * @see StructuredReader#readName()
   * @see StructuredWriter#writeName(String)
   */
  protected String name;

  /** @see #getState() */
  StructuredState state;

  /** The current {@link StructuredNode}. */
  protected S node;

  /**
   * The constructor.
   *
   * @param format the {@link #getFormat() format}.
   */
  public AbstractStructuredProcessor(StructuredFormat format) {

    super();
    this.format = format;
    this.config = format.getConfig();
    this.enumFormat = this.config.get(MarshallingConfig.VAR_ENUM_FORMAT);
    this.state = StructuredState.NULL;
    this.node = newNode(null, null); // root state
  }

  /**
   * @param type the {@link StructuredNodeType}.
   * @param object the {@link StructuredIdMappingObject}. Will be {@code null} for {@link StructuredNodeType#ARRAY}.
   * @return the new child {@link StructuredNode}.
   */
  protected abstract S newNode(StructuredNodeType type, StructuredIdMappingObject object);

  @Override
  public StructuredFormat getFormat() {

    return this.format;
  }

  @Override
  public StructuredState getState() {

    return this.state;
  }

  /**
   * Set the new {@link #getState() state} and checks if the
   * {@link StructuredState#isValidTransition(StructuredState, StructuredNodeType) transition is valid}.
   *
   * @param newState the new {@link #getState() state} to set.
   * @return the given {@link StructuredState} that has been set. May be used for fluent API calls.
   */
  protected StructuredState setState(StructuredState newState) {

    // accept intermediate transitions from to NULL (edge-case for gRPC)
    if (newState != StructuredState.NULL && (this.node != null)
        && ((this.state != StructuredState.NULL) || (this.node.parent == null))) {
      StructuredNodeType type = this.node.getType();
      if (!this.state.isValidTransition(newState, type)) {
        StringBuilder sb = new StringBuilder(type == null ? 56 : 72);
        sb.append("Invalid transition from state ");
        sb.append(this.state);
        sb.append(" to ");
        sb.append(newState);
        if (type != null) {
          sb.append(" in parent ");
          sb.append(type);
        }
        if (this.name != null) {
          sb.append(" at property ");
          sb.append(this.name);
          int id = getId();
          if (id > 0) {
            sb.append('[');
            sb.append(id);
            sb.append(']');
          }
        }
        sb.append('!');
        throw new IllegalStateException(sb.toString());
      }
    }
    this.state = newState;
    return this.state;
  }

  @Override
  public String getName() {

    return this.name;
  }

  /**
   * @return the potential ID corresponding to the {@link #getName() name} for the current property of
   *         {@link StructuredFormat#isIdBased() ID based} {@link StructuredFormat formats}. For other formats this
   *         method will always return {@code 0}.
   */
  public int getId() {

    return 0;
  }

  @Override
  public final void close() {

    if (this.node != null) {
      try {
        doClose();
      } catch (IOException e) {
        throw new RuntimeIoException(e);
      }
    }
    // setState(StructuredState.DONE);
    this.state = StructuredState.DONE;
    this.node = null;
  }

  /**
   * Called once to close this writer and free resources.
   *
   * @throws IOException on error.
   */
  protected abstract void doClose() throws IOException;

}
