package com.example.demo.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.response.BaseResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: boot-shiro
 * @description:
 * @author: 001977
 * @create: 2018-07-17 20:03
 */
@Component
public class ExceptionInterceptor implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String xmlHttpRequest = httpServletRequest.getHeader("X-Requested-With");
        e.printStackTrace();
        if(xmlHttpRequest != null){	//Ajax请求
            System.out.println("MyExceptionResolver--Ajax异常");
            System.out.println(e);
            try {
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("application/json");
                PrintWriter writer = httpServletResponse.getWriter();
                BaseResponse res = new BaseResponse(1,e.getMessage());
                writer.write(JSONObject.toJSONString(res));
                writer.flush();
            } catch (IOException e1) {
                System.out.println("获取输出流出错");
            }
            return new ModelAndView();	//这一步不能返回null
        }else{
            System.out.println("MyExceptionResolver--URL异常");
            System.out.println(e);
            //如果是文件异常，Ajax异步上传，要返回JSON
            if(e instanceof MaxUploadSizeExceededException){
                try {
                    httpServletResponse.setCharacterEncoding("UTF-8");
                    httpServletResponse.setContentType("application/json");
                    PrintWriter writer = httpServletResponse.getWriter();
                    BaseResponse res = new BaseResponse(1,"文件超过限制大小");
                    writer.write(JSONObject.toJSONString(res));
                    writer.flush();
                } catch (IOException e1) {
                    System.out.println("获取输出流出错");
                }
                return new ModelAndView();
            }
            ModelAndView mv = new ModelAndView("error");
            mv.addObject("exception", e);
            return mv;
        }
    }
}
