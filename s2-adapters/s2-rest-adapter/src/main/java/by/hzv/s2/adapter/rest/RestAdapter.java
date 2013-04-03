package by.hzv.s2.adapter.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import by.hzv.s2.service.S2;


/**
 * @author <a href="mailto:dkotsubo@wiley.com">Dmitry Kotsubo</a>
 * @since 01.04.2013
 */
@Controller
public class RestAdapter {
    @Autowired
    private S2 s2;
}
