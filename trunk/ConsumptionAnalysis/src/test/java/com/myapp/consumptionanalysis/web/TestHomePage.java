package com.myapp.consumptionanalysis.web;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import com.myapp.consumptionanalysis.web.HomePage;
import com.myapp.consumptionanalysis.web.WicketApplication;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{
    private WicketTester tester;

    @Before
    public void setUp() {
        tester = new WicketTester(new WicketApplication());
    }

    @Test
    public void homepageRendersSuccessfully() {
        //start and render the test page
        PageParameters pp = new PageParameters();
        pp.add("project", "");
        tester.startPage(HomePage.class, pp);

        //assert rendered page class
        tester.assertRenderedPage(HomePage.class);
    }
}
