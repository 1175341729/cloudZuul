package com.cloud.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class AccessFilter extends ZuulFilter {
    private Logger logger = LoggerFactory.getLogger(AccessFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try {
            HttpServletRequest request = ctx.getRequest();
            String token = request.getParameter("token");
            if (StringUtils.isBlank(token)) throw new RuntimeException("token is null");
            logger.info("{}","正常流程！");
            String method = request.getMethod();
            System.out.println(method + "---");

            if ("GET".equals(method)){
                Map<String, String[]> parameterMap = request.getParameterMap();
            } else if ("POST".equals(method)){
                // 获取post请求参数类容
                if (!ctx.isChunkedRequestBody()){
                    ServletInputStream inp;
                    try {
                        inp = ctx.getRequest().getInputStream();
                        String body;
                        if (inp != null) {
                            body = IOUtils.toString(inp);
                            System.err.println("REQUEST:: > " + body);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                ctx.getResponse().getWriter().write("token is null");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
