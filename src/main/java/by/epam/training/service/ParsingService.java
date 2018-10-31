package by.epam.training.service;

import java.util.Set;

public interface ParsingService {

    Set<String> parse(String value);

    Set<String> parseParallel(String value);

}
