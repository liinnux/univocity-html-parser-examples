/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples;

import com.univocity.api.common.*;
import com.univocity.api.entity.html.*;
import com.univocity.api.entity.html.builders.*;
import com.univocity.parsers.common.processor.core.*;
import com.univocity.parsers.common.record.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.html.processor.*;
import org.testng.annotations.*;

import java.util.*;

/**
 * Created by anthony on 24/06/16.
 */
public class HtmlParserExamples extends Example {

	private void printOutput(Map<String, List<String[]>> allRows) {
		for (Map.Entry<String, List<String[]>> e : allRows.entrySet()) {
			println("Rows in entity name: " + e.getKey());
			println("-- ROWS --");
			for (String[] row : e.getValue()) {
				println(Arrays.toString(row));
			}
		}
	}

	@Test
	public void example001ParseAll() throws Exception {
		//##CODE_START
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);
		settings.setDownloadContentDirectory("{user.home}/Downloads/pages/");

		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);

		UrlReaderProvider input = new UrlReaderProvider("http://www.ikea.com/au/en/search/?query=cup");

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(input);
		printOutput(allRows);

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example001ParseAllFromFile() throws Exception {
		//##CODE_START
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);

		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(new FileProvider("{user.home}/Downloads/pages/page1.html"));
		printOutput(allRows);


		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example002ReadHtmlWithRowProcessor() throws Exception {
		//##CODE_START

		// The settings object provides many configuration options
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);

		// A RowListProcessor stores each parsed row in a List.
		RowListProcessor rowProcessor = new RowListProcessor();

		// You can configure the parser to use a RowProcessor to process the values of each parsed row.
		// You will find more RowProcessors in the 'com.univocity.parsers.common.processor' package, but you can also create your own.

		HtmlEntitySettings entitySettings = htmlEntityList.getEntity("items");
		entitySettings.setProcessor(rowProcessor);

		// creates a parser instance with the given settings
		HtmlParser parser = new HtmlParser(settings);

		// the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
		parser.parse(new FileProvider("inputs/page1.html", "UTF-8"));

		// get the parsed records from the RowListProcessor here.
		// Note that different implementations of RowProcessor will provide different sets of functionalities.
		String[] headers = rowProcessor.getHeaders();
		List<String[]> rows = rowProcessor.getRows();

		println(Arrays.toString(headers));
		for (String[] row : rows) {
			println(Arrays.toString(row));
		}
		//##CODE_END

		printAndValidate();
	}

	@Test
	public void example003ReadHtmlAndConvertValues() throws Exception {

		//##CODE_START
		HtmlEntityList htmlEntityList = configure();

		// ObjectRowProcessor converts the parsed values and gives you the resulting row.
		ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
			@Override
			public void rowProcessed(Object[] objects, HtmlParsingContext parsingContext) {
				println(Arrays.toString(objects));
			}
		};

		// converts values in the "price" column (index 3) to Double, after removing dollar sign and any letters
		rowProcessor.convertIndexes(Conversions.replace("\\$", ""), Conversions.replace("/.*", ""), Conversions.toDouble()).set(3);

		// converts the values in columns "name" to lower case.
		rowProcessor.convertFields(Conversions.toLowerCase()).set("name");

		HtmlParserSettings parserSettings = new HtmlParserSettings(htmlEntityList);
		htmlEntityList.configureEntity("items").setProcessor(rowProcessor);


		HtmlParser parser = new HtmlParser(parserSettings);

		//the rowProcessor will be executed here.
		parser.parse(new FileProvider("inputs/page1.html", "UTF-8"));

		//##CODE_END

		printAndValidate();
	}

	@Test
	public void example004UsingAnnotations() throws Exception {
		//##CODE_START
		// BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
		BeanListProcessor<Items> rowProcessor = new BeanListProcessor<Items>(Items.class);
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings parserSettings = new HtmlParserSettings(htmlEntityList);

		htmlEntityList.configureEntity("items").setProcessor(rowProcessor);

		HtmlParser parser = new HtmlParser(parserSettings);
		parser.parse(new FileProvider("inputs/page1.html", "UTF-8"));

		// The BeanListProcessor provides a list of objects extracted from the input.
		List<Items> beans = rowProcessor.getBeans();
		for (Items item : beans) {
			println(item);
		}

		//##CODE_END

		printAndValidate();
	}

	@Test
	public void example005ParseAllRecords() throws Exception {
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);

		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);


		// parses all records in one go.
		Map<String, List<Record>> allRecords = parser.parseAllRecords(new FileProvider("inputs/page1.html", "UTF-8"));
		for (Map.Entry<String, List<Record>> record : allRecords.entrySet()) {
			println("Entity name: " + record.getKey());

			for (Record r : record.getValue()) {
				print(r.getString("name") + ", ");
				print(r.getString("URL") + ", ");
				print(r.getString("desc") + ", ");
				print(r.getString("price"));
				print(r.getString("image"));
				println();
			}

		}

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example006ParseWithFieldSelection() throws Exception {
		//##CODE_START
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);

		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);

		//selects which fields will be returned, and in what order
		htmlEntityList.configureEntity("items").selectFields("price", "name");

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(new FileProvider("inputs/page1.html", "UTF-8"));

		printOutput(allRows);


		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example007FieldSelectionWithoutReordering() {
		//##CODE_START
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);
		settings.setNullValue("\\N");

		HtmlEntitySettings items = htmlEntityList.configureEntity("items");
		items.setProcessor(new AbstractProcessor<HtmlParsingContext>() {
			public void rowProcessed(String[] row, HtmlParsingContext context) {
				println(Arrays.toString(row));
			}
		});
		items.selectIndexes(3, 1);
		items.setColumnReorderingEnabled(false);

		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);

		parser.parse(new FileProvider("inputs/page1.html", "UTF-8"));


		//##CODE_END
		printAndValidate();
	}


	@Test
	public void example008Pagination() {
		//##CODE_START
		HtmlEntityList entityList = configure();

		HtmlParserSettings settings = new HtmlParserSettings(entityList);

		//Pagination allows the parser to go to the next page after parsing a page
		settings.configurePaginator().newGroup().startAt("div").classes("filterDropdowns").endAt("div").classes("serpSearchString").setNextPage().match("div").id("pagination").match("a").precededImmediatelyBy("a").classes("active").getAttribute("href");

		//Sets where content will be downloaded to and sets a filename pattern for pages downloaded
		settings.setDownloadContentDirectory("ikea/");
		settings.setFileNamePattern("search/{pageNumber}");

		HtmlParser parser = new HtmlParser(settings);

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(new FileProvider("inputs/page1.html", "UTF-8"));
		printOutput(allRows);


		//##CODE_END
		printAndValidate();

	}

	@Test
	public void example009ItemFollow() {
		HtmlEntityList entityList = new HtmlEntityList();
		HtmlParserSettings settings = new HtmlParserSettings(entityList);

		HtmlEntitySettings items = entityList.configureEntity("items");

		PartialHtmlPath path = items.newPath().match("table").id("productsTable").match("td").match("div").classes("productContainer");

		path.addField("name").match("span").classes("prodName", "prodNameTro").getText();
		path.addField("price").match("span").classes("prodPrice").getText();

		settings.configurePaginator().newGroup().startAt("div").classes("filterDropdowns").endAt("div").classes("serpSearchString").setNextPage().match("div").id("pagination").match("a").precededImmediatelyBy("a").classes("active").getAttribute("href");
		settings.configurePaginator().setFollowCount(1);

		HtmlLinkFollower linkFollower = settings.configureLinkFollower();
		linkFollower.setJoinRows(true);
		linkFollower.addLink().match("table").id("productsTable").match("td").match("div").classes("productContainer").match("div").classes("parentContainer").match("a").getAttribute("href");
		linkFollower.addField("fullDesc").match("div").id("salesArg").match("a").getPrecedingText();
		linkFollower.addField("goodToKnow").match("div").id("careInst").getText();

		HtmlParser parser = new HtmlParser(settings);

		printOutput(parser.parseAll(new FileProvider("inputs/page1.html", "UTF-8")));

		printAndValidate();
	}


	protected HtmlEntityList configure() {
		HtmlEntityList entityList = new HtmlEntityList();

		HtmlEntitySettings items = entityList.configureEntity("items");
		PartialHtmlPath path = items.newPath().match("table").id("productsTable").match("td").match("div").classes("productContainer");
		path.addField("name").match("span").classes("prodName", "prodNameTro").getText();
		path.addField("URL").match("a").childOf("div").classes("productPadding").getAttribute("href");
		path.addField("desc").match("span").classes("prodDesc").getText();
		path.addField("price").match("span").classes("prodPrice").getText();
		path.addField("image").match("img").classes("prodImg").getContentFrom("src");

		return entityList;
	}


}
