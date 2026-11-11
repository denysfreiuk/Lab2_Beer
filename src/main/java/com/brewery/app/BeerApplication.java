package com.brewery.app;

import com.brewery.model.Beer;
import com.brewery.parser.ParserType;
import com.brewery.transform.XslTransformer;
import com.brewery.validation.ValidationResult;
import com.brewery.validation.XmlValidator;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The scenario of the laboratory work: validate the document, read it with
 * every parser, sort the beers and regroup the document with XSL.
 */
public final class BeerApplication {

    private static final Logger LOG = LogManager.getLogger(BeerApplication.class);

    private final XmlValidator validator;
    private final XslTransformer transformer;

    public BeerApplication() {
        this(new XmlValidator(), new XslTransformer());
    }

    BeerApplication(XmlValidator validator, XslTransformer transformer) {
        this.validator = validator;
        this.transformer = transformer;
    }

    /**
     * @return {@code true} if the whole scenario has been completed,
     *         {@code false} if the document is not valid and has not been processed
     */
    public boolean run(AppConfig config) {
        ValidationResult validation = validator.validate(config.xml(), config.xsd());
        if (!validation.isValid()) {
            validation.errors().forEach(error -> LOG.error("  {}", error));
            return false;
        }

        Map<ParserType, List<Beer>> parsed = parseWithEveryParser(config);
        long distinctResults = parsed.values().stream().distinct().count();
        LOG.info("All parsers produced the same collection: {}", distinctResults == 1);

        transformer.transform(config.xml(), config.xsl(), config.output());
        return true;
    }

    /** Reads the document with each parser and returns the beers sorted in the configured order. */
    Map<ParserType, List<Beer>> parseWithEveryParser(AppConfig config) {
        Map<ParserType, List<Beer>> parsed = new EnumMap<>(ParserType.class);
        for (ParserType type : ParserType.values()) {
            List<Beer> beers = sorted(type.createParser().parse(config.xml()), config);
            LOG.info("{} parser, {} beers sorted {}:", type, beers.size(), config.sortOrder());
            beers.forEach(beer -> LOG.info("  {}", beer));
            parsed.put(type, beers);
        }
        return parsed;
    }

    private static List<Beer> sorted(List<Beer> beers, AppConfig config) {
        List<Beer> copy = new ArrayList<>(beers);
        copy.sort(config.sortOrder());
        return copy;
    }
}
