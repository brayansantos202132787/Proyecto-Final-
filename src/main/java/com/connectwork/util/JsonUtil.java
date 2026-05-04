/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


/**
 *
 * @author braya
 */
public class JsonUtil {
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private JsonUtil() {}

    
    public static <T> T fromRequest(HttpServletRequest req, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return GSON.fromJson(sb.toString(), clazz);
    }

    
    public static void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.print(GSON.toJson(data));
        writer.flush();
    }

    // Respuesta de error estandarizada
    public static void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        sendJson(resp, status, new ErrorResponse(message));
    }

    // Respuesta de éxito estandarizada
    public static void sendSuccess(HttpServletResponse resp, int status, String message, Object data) throws IOException {
        sendJson(resp, status, new SuccessResponse(message, data));
    }

    // Clases internas para estructurar respuestas
    private static class ErrorResponse {
        boolean success = false;
        String message;
        ErrorResponse(String m) { this.message = m; }
    }

    private static class SuccessResponse {
        boolean success = true;
        String message;
        Object data;
        SuccessResponse(String m, Object d) { this.message = m; this.data = d; }
    }
}
