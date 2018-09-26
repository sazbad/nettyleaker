package com.adobe.netty;

   /*
    * Copyright 2012 The Netty Project
    *
    * The Netty Project licenses this file to you under the Apache License,
    * version 2.0 (the "License"); you may not use this file except in compliance
    * with the License. You may obtain a copy of the License at:
    *
    *   http://www.apache.org/licenses/LICENSE-2.0
    *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
   * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
   * License for the specific language governing permissions and limitations
   * under the License.
   */

  import io.netty.buffer.ByteBuf;
  import io.netty.buffer.ByteBufAllocator;
  import io.netty.buffer.Unpooled;
  import io.netty.channel.ChannelDuplexHandler;
  import io.netty.channel.ChannelFutureListener;
  import io.netty.channel.ChannelHandlerContext;
  import io.netty.channel.SimpleChannelInboundHandler;
  import io.netty.handler.codec.DecoderResult;
  import io.netty.handler.codec.http.*;
  import io.netty.handler.codec.http.cookie.Cookie;
  import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
  import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
  import io.netty.util.CharsetUtil;
  import io.netty.util.ReferenceCountUtil;

  import java.util.List;
  import java.util.Map;
  import java.util.Map.Entry;
  import java.util.Set;

  import static io.netty.handler.codec.http.HttpResponseStatus.*;
  import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
  import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
  import static io.netty.handler.codec.http.HttpResponseStatus.OK;
  import static io.netty.handler.codec.http.HttpVersion.*;
  import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpSnoopServerHandler extends ChannelDuplexHandler {

    private HttpRequest request;
    /** Buffer that stores the response content */
    private final StringBuilder buf = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


        System.out.println(msg.getClass().toString());

        if ( !((FullHttpRequest)msg).decoderResult().isSuccess()) {
            //sendErrorResponse(channelHandlerContext, errorResponseWriter, "this is a weird one", BAD_REQUEST, ERROR_TITLE);
            return;
        }


      // If you enable HttpObjectAggeratgor then you enable this block

        if(msg instanceof FullHttpRequest) {

           if (((FullHttpRequest) msg).decoderResult().isFailure()) {
               System.out.println("Got Failure in decode result");
           }
            try {

                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } finally {
                ReferenceCountUtil.release(msg);
            }

        }

   }

   private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
       DecoderResult result = o.decoderResult();
       if (result.isSuccess()) {
           return;
       }

       buf.append(".. WITH DECODER FAILURE: ");
       buf.append(result.cause());
       buf.append("\r\n");
   }

   private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
       // Decide whether to close the connection or not.
       boolean keepAlive = HttpUtil.isKeepAlive(request);
       // Build the response object.
       FullHttpResponse response = new DefaultFullHttpResponse(
               HTTP_1_1, currentObj.decoderResult().isSuccess()? OK : BAD_REQUEST,
               Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

       response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

       if (keepAlive) {
           // Add 'Content-Length' header only for a keep-alive connection.
           response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
           // Add keep alive header as per:
           // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
           response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
       }

       // Encode the cookie.
       String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
       if (cookieString != null) {
           Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
           if (!cookies.isEmpty()) {
               // Reset the cookies if necessary.
               for (Cookie cookie: cookies) {
                   response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
               }
           }
       } else {
           // Browser sent no cookie.  Add some.
           response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key1", "value1"));
           response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
       }

       // Write the response.
       ctx.write(response);

       return keepAlive;
   }

   private static void send100Continue(ChannelHandlerContext ctx) {
       FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
       ctx.write(response);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
       cause.printStackTrace();
       ctx.close();
   }
}