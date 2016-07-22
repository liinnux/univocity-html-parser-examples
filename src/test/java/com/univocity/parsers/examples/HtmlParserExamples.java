package com.univocity.parsers.examples;

import com.univocity.api.common.*;
import com.univocity.api.entity.html.*;
import com.univocity.api.entity.html.builders.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;
import com.univocity.parsers.common.record.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.html.parser.builder.*;
import com.univocity.parsers.html.processor.*;
import org.testng.annotations.*;

import java.util.*;

/**
 * Created by anthony on 24/06/16.
 */
public class HtmlParserExamples extends Example {

	private UrlReaderProvider getInput() {
		return new UrlReaderProvider("http://www.ikea.com/au/en/search/?query=cup");
	}

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
		settings.setDownloadContentDirectory("images/");
		//settings.selectIndexes(3,1);
		settings.setColumnReorderingEnabled(false);
		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(getInput());
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
		settings.setEntityProcessor(rowProcessor, "items");

		// creates a parser instance with the given settings
		HtmlParser parser = new HtmlParser(settings);

		// the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
		parser.parse(getInput());

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
		rowProcessor.convertIndexes(Conversions.replace("\\$", "")).set(3);
		rowProcessor.convertIndexes(Conversions.replace("/.*", "")).set(3);
		rowProcessor.convertIndexes(Conversions.toDouble()).set(3);

		// converts the values in columns "name" to lower case.
		rowProcessor.convertFields(Conversions.toLowerCase()).set("name");

		HtmlParserSettings parserSettings = new HtmlParserSettings(htmlEntityList);
		parserSettings.setEntityProcessor(rowProcessor, "items");

		HtmlParser parser = new HtmlParser(parserSettings);

		//the rowProcessor will be executed here.
		parser.parse(getInput());

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

		parserSettings.setEntityProcessor(rowProcessor, "items");

		HtmlParser parser = new HtmlParser(parserSettings);
		parser.parse(getInput());

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
		Map<String, List<Record>> allRecords = parser.parseAllRecords(getInput());
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
	public void example006ParseAll() throws Exception {
		//##CODE_START
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);

		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);

		//selects which fields will be returned, and in what order
		settings.selectFields("price", "name");

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(getInput());

		printOutput(allRows);


		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example007GlobalRowProcessor() {
		//##CODE_START
		HtmlEntityList htmlEntityList = configure();
		HtmlParserSettings settings = new HtmlParserSettings(htmlEntityList);

		// sets a global row processor
		settings.setGlobalProcessor(new AbstractProcessor() {
			public void rowProcessed(String[] row, ParsingContext context) {
				super.rowProcessed(row, context);
				System.out.println(row[0]);
			}
		});
		settings.selectIndexes(3, 1);
		settings.setColumnReorderingEnabled(false);

		// creates a HTML parser
		HtmlParser parser = new HtmlParser(settings);

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(getInput());

		printOutput(allRows);


		//##CODE_END
		printAndValidate();
	}



	@Test
	public void example008Pagination() {
		//##CODE_START
		HtmlEntityList entityList = configure();

		//Pagination allows the parser to go to the next page after parsing a page
		entityList.configurePaginator().newGroup().startAt("div").classes("filterDropdowns").endAt("div").classes("serpSearchString").setNextPage().match("div").id("pagination").match("a").precededImmediatelyBy("a").classes("active").getAttribute("href");

		HtmlParserSettings settings = new HtmlParserSettings(entityList);

		//Sets where content will be downloaded to and sets a filename pattern for pages downloaded
		settings.setDownloadContentDirectory("ikea/");
		settings.setFileNamePattern("search/{pageNumber}");

		HtmlParser parser = new HtmlParser(settings);

		// parses all rows in one go.
		Map<String, List<String[]>> allRows = parser.parseAll(getInput());
		printOutput(allRows);


		//##CODE_END
		printAndValidate();

	}

	@Test
	public void example009ItemFollow() {
		HtmlEntityList entityList = new HtmlEntityList();
		HtmlParserSettings settings = new HtmlParserSettings(entityList);

		HtmlEntity items = entityList.configureEntity("items");

		PartialHtmlPath path = items.newPath().match("table").id("productsTable").match("td").match("div").classes("productContainer");

		path.addField("name").match("span").classes("prodName", "prodNameTro").getText();
		path.addField("price").match("span").classes("prodPrice").getText();

		entityList.configurePaginator().newGroup().startAt("div").classes("filterDropdowns").endAt("div").classes("serpSearchString").setNextPage().match("div").id("pagination").match("a").precededImmediatelyBy("a").classes("active").getAttribute("href");
		entityList.configurePaginator().setFollowCount(1);

		HtmlLinkFollower linkFollower = entityList.configureLinkFollower();
		linkFollower.setJoinRows(true);
		linkFollower.addLink().match("table").id("productsTable").match("td").match("div").classes("productContainer").match("div").classes("parentContainer").match("a").getAttribute("href");
		linkFollower.addField("fullDesc").match("div").id("salesArg").match("a").getPrecedingText();
		linkFollower.addField("goodToKnow").match("div").id("careInst").getText();

		HtmlParser parser = new HtmlParser(settings);

		printOutput(parser.parseAll(getInput()));

		printAndValidate();
	}


	protected HtmlEntityList configure() {
		HtmlEntityList entityList = new HtmlEntityList();

		UrlReaderProvider provider = getInput();

		HtmlEntity items = entityList.configureEntity("items");
		PartialHtmlPath path = items.newPath().match("table").id("productsTable").match("td").match("div").classes("productContainer");
		path.addField("name").match("span").classes("prodName", "prodNameTro").getText();
		path.addField("URL").match("a").childOf("div").classes("productPadding").getAttribute("href");
		path.addField("desc").match("span").classes("prodDesc").getText();
		path.addField("price").match("span").classes("prodPrice").getText();
		path.addField("image").match("img").classes("prodImg").getContentFrom("src");

		return entityList;
	}


}
