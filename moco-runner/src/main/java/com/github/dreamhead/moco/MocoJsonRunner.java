package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;
import com.github.dreamhead.moco.parser.SocketServerParser;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.collect.ImmutableList;

import java.io.InputStream;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class MocoJsonRunner {
    private static final HttpServerParser PARSER = new HttpServerParser();

    public static HttpServer jsonHttpServer(final int port, final Resource resource) {
        checkArgument(port > 0, "Port must be greater than zero");
        return parseHttpServer(checkNotNull(resource, "resource should not be null"), of(port));
    }

    public static HttpServer jsonHttpServer(final Resource resource) {
        return parseHttpServer(checkNotNull(resource, "resource should not be null"), empty());
    }

    public static HttpsServer jsonHttpsServer(final Resource resource,
                                              final HttpsCertificate certificate) {
        checkNotNull(certificate, "Certificate should not be null");
        ActualHttpServer httpsServer = (ActualHttpServer) Moco.httpsServer(certificate);
        return httpsServer.mergeServer((ActualHttpServer) parseHttpServer(
                checkNotNull(resource, "resource should not be null"), empty()));
    }

    public static HttpsServer jsonHttpsServer(final int port, final Resource resource,
                                              final HttpsCertificate certificate) {
        checkArgument(port > 0, "Port must be greater than zero");
        checkNotNull(certificate, "Certificate should not be null");
        ActualHttpServer httpsServer = (ActualHttpServer) Moco.httpsServer(port, certificate);
        return httpsServer.mergeServer((ActualHttpServer) parseHttpServer(
                 checkNotNull(resource, "resource should not be null"), of(port)));
    }

    public static SocketServer jsonSocketServer(final int port, final Resource resource) {
        checkArgument(port > 0, "Port must be greater than zero");
        return jsonSocketServer(checkNotNull(resource, "resource should not be null"), of(port));
    }

    public static SocketServer jsonSocketServer(final Resource resource) {
        return jsonSocketServer(checkNotNull(resource, "resource should not be null"), empty());
    }

    private static SocketServer jsonSocketServer(final Resource resource, final Optional<Integer> port) {
        SocketServerParser parser = new SocketServerParser();
        return parser.parseServer(ImmutableList.of(
                toStream(checkNotNull(resource, "resource should not be null"))), port);
    }

    private static HttpServer parseHttpServer(final Resource resource, final Optional<Integer> port) {
        return PARSER.parseServer(ImmutableList.of(toStream(resource)), port);
    }

    private static InputStream toStream(final Resource resource) {
        return checkNotNull(resource, "resource should not be null").readFor(null).toInputStream();
    }

    private MocoJsonRunner() {
    }
}
