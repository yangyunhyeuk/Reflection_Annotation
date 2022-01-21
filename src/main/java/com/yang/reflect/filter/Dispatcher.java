package com.yang.reflect.filter;


import com.yang.reflect.anno.RequestMapping;
import com.yang.reflect.controller.UserController;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

public class Dispatcher implements Filter {

    boolean flag = false;

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException {
        System.out.println("디스패쳐 진입1");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
/*

        System.out.println("컨텍스트 패스 : " + request.getContextPath()); // 프로젝트 시작주소
        System.out.println("식별자 주소 : " + request.getRequestURI()); // 끝주소
        System.out.println("전체주소 : " + request.getRequestURL()); // 전체주소
*/


        // user 파싱하기
        String endPoint = request.getRequestURI().replaceAll(request.getContextPath(), "");
        System.out.println("endPoint : " + endPoint);

        UserController userController = new UserController();
/*

        if (endPoint.equals("/join")) {
            userController.join();
        } else if (endPoint.equals("/login")) {
            userController.login();
        }
*/

        // 선언된 메서드를 다음의 배열 변수에 저장
        // 리플렉션 => 메서드를 런타임 시점에 찾아내서 실행
        Method[] methods = userController.getClass().getDeclaredMethods();
        // Method[] method = userController.getClass().getMethods(); // 오브젝트 격인 부모 메서드도 저장된다.

        /*
        for (Method method : methods) {
            // System.out.println(method.getName());
            if (endPoint.equals("/" + method.getName())) {
                try {
                    method.invoke(userController);
                    // 해당 메서드를 호출
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        */

        for (Method method : methods) {
            Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
            RequestMapping requestMapping = (RequestMapping) annotation;
            // annotation이 들고 있는 함수와 requestMapping가 들고있는 타입과 다르다
            // .value() 메서드를 호출하기 위해 다운캐스팅을 진행한 것이다.

            System.out.println("annotation : " + requestMapping.value());

            if (requestMapping.value().equals(endPoint)) {
                flag = true;
                try {
// 먼저 파라미터를 분석한다.
                    Parameter[] params = method.getParameters();
                    String path = null;
                    if (params.length != 0) { // 파라미터가 존재
/*
                        System.out.println("params[0].getType() : " + params[0].getType());
                        // params[0].getType() : class com.yang.reflect.controller.dto.LoginDto
                        // == LoginDto.class.newInstance();
*/
                        // 해당 dtoInstance를 reflection하여 set함수를 호출
                        // 이 때 요청받은 username, password를 기반으로 호출한다.
                        Object dtoInstance = params[0].getType().newInstance();

                        // String username = request.getParameter("username");
                        // String password = request.getParameter("password");

                        setData(dtoInstance, request);
                        path = (String) method.invoke(userController, dtoInstance);


                    } else { // 파라미터가 없다
                        path = (String) method.invoke(userController);
                    }

                    RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
                    requestDispatcher.forward(request, response);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (flag == false) {
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println("잘못된 주소 요청입니다, error : 404");
            out.flush();
        }
    }

    private <T> void setData(T instance, HttpServletRequest request) {
        Enumeration<String> keys = request.getParameterNames(); // 크기 2개 username, password
        // key값을 변형하여 username => setUsername
        // key값을 변형하여 password => setPassword
        while (keys.hasMoreElements()) { // 2번 돈다
            String key = (String) keys.nextElement();
            String methodKey = keyToMethodKey(key); // setUsername 변환 함수

            Method[] methods = instance.getClass().getDeclaredMethods(); // 5개

            for (Method method : methods) {
                if (method.getName().equals(methodKey)) {
                    try {
                        method.invoke(instance, request.getParameter(key)); // request.getParameter(key)는 String 타입
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String keyToMethodKey(String key) {
        String firstKey = "set";
        String upperKey = key.substring(0, 1).toUpperCase();
        String remainKey = key.substring(1);

        String methodKey = firstKey + upperKey + remainKey;
        return methodKey;
    }


    public void destroy() {

    }

}