package helloworld.controller;

import java.util.Map;

public interface IController {
    Object handle(Map<String, Object> input) throws Exception;
}
