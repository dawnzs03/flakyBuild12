package io.quarkus.redis.datasource.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.quarkus.redis.datasource.RedisCommandExtraArguments;

/**
 * The argument of the <a href="http://redis.io/commands/xadd">XADD</a> command.
 */
public class XAddArgs implements RedisCommandExtraArguments {

    private String id;

    private long maxlen = -1;

    private boolean approximateTrimming;

    private boolean nomkstream;

    private String minid;

    private long limit = -1;

    /**
     * Sets the stream id to identify a given entry inside a stream.
     * If not set, the stream id is generated by the Redis server.
     *
     * @param id the id, must not be {@code null}, but be formed by two numbers separated by a {@code -}. In general,
     *        the first number is a timestamp.
     * @return the current {@code XAddArgs}
     */
    public XAddArgs id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the max length of the stream.
     * When {@code XADD} is called with this parameter, the new entry is added to the stream, but if the max size is
     * reached, the oldest entry is evicted.
     *
     * @param maxlen the max length of the stream, must be positive
     * @return the current {@code XAddArgs}
     */
    public XAddArgs maxlen(Long maxlen) {
        this.maxlen = maxlen;
        return this;
    }

    /**
     * When set, prefix the {@link #maxlen} with {@code ~} to enable the <em>almost exact trimming</em>.
     * This is recommended when using {@link #maxlen(Long)}.
     *
     * @return the current {@code XAddArgs}
     */
    public XAddArgs nearlyExactTrimming() {
        this.approximateTrimming = true;
        return this;
    }

    /**
     * Do not create a new stream if the stream does not exist yet.
     *
     * @return the current {@code XAddArgs}
     */
    public XAddArgs nomkstream() {
        this.nomkstream = true;
        return this;
    }

    /**
     * Evicts entries from the stream having IDs lower to the specified one.
     *
     * @param minid the min id, must not be {@code null}, must be a valid stream id
     * @return the current {@code XAddArgs}
     */
    public XAddArgs minid(String minid) {
        this.minid = minid;
        return this;
    }

    /**
     * Sets the maximum entries that can get evicted.
     *
     * @param limit the limit, must be positive
     * @return the current {@code XAddArgs}
     */
    public XAddArgs limit(long limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public List<String> toArgs() {
        List<String> args = new ArrayList<>();
        if (nomkstream) {
            args.add("NOMKSTREAM");
        }

        if (maxlen > 0) {
            if (minid != null) {
                throw new IllegalArgumentException("Cannot use `MAXLEN` and `MINID` together");
            }

            args.add("MAXLEN");
            if (approximateTrimming) {
                args.add("~");
            } else {
                args.add("=");
            }
            args.add(Long.toString(maxlen));
        }

        if (minid != null) {
            args.add("MINID");
            if (approximateTrimming) {
                args.add("~");
            } else {
                args.add("=");
            }
            args.add(minid);
        }

        if (limit > 0) {
            if (!approximateTrimming) {
                throw new IllegalArgumentException("Cannot set the eviction limit when using exact trimming");
            }
            args.add("LIMIT");
            args.add(Long.toString(limit));
        }

        args.add(Objects.requireNonNullElse(id, "*"));
        return args;
    }
}
