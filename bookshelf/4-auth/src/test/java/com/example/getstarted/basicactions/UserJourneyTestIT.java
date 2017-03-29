/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.getstarted.basicactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.cloud.datastore.Batch;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class UserJourneyTestIT {

  private static final String TITLE = "mytitle";
  private static final String AUTHOR = "myauthor";
  private static final String PUBLISHED_DATE = "1984-02-27";
  private static final String DESCRIPTION = "mydescription";

  private static DriverService service;
  private WebDriver driver;

  @BeforeClass
  public static void setupClass() throws Exception {
    service = ChromeDriverService.createDefaultService();
    service.start();

  }

  @AfterClass
  public static void tearDownClass() {
    service.stop();

    // Clear the datastore
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Batch batch = datastore.newBatch();
    StructuredQuery<Key> query = Query.newKeyQueryBuilder()
        .setKind("Book4").build();
    for (QueryResults<Key> keys = datastore.run(query); keys.hasNext(); ) {
      batch.delete(keys.next());
    }
    batch.submit();
  }

  @Before
  public void setup() {
    driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  private WebElement checkLandingPage() throws Exception {
    WebElement button = driver.findElement(By.linkText("Login"));
    assertTrue(null != button);

    WebElement list = driver.findElement(By.cssSelector("body>.container p"));
    assertEquals("No books found", list.getText());

    return button;
  }

  @Test
  public void userJourney() throws Exception {
    driver.get("http://localhost:8080");

    try {
      WebElement loginButton = checkLandingPage();

      loginButton.click();
      (new WebDriverWait(driver, 10)).until(
          ExpectedConditions.urlMatches("https://accounts.google.com"));

      // ...aaaaand that's about as far as I can test without a Real Account.
    } catch (Exception e) {
      System.err.println(driver.getPageSource());
      throw e;
    }
  }
}