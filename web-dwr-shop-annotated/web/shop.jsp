<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%--
    Document   : shop.jsp
    Created on : 06.01.2009
    Author     : andre
--%>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>testapplikation f&uuml;r dwr</title>

        <%String ctxtRoot = config.getServletContext().getContextPath();%>
        <script type='text/javascript' src="<%=ctxtRoot%>/dwr/interface/Store.js"></script>
        <script type='text/javascript' src="<%=ctxtRoot%>/dwr/interface/ShoppingBasket.js"></script>
        <script type='text/javascript' src="<%=ctxtRoot%>/dwr/engine.js"></script>
        <script type='text/javascript' src="<%=ctxtRoot%>/dwr/util.js"></script>

    </head>
    <body>
        <h2>Webshop using DWR</h2>
        <form id="searchform">
            <input id="searchbox"/>
            <button type="submit" id="searchbtn">Suchen</button>
        </form>
        <div>
            <h3>Ihr Einkaufskorb:</h3>
            <ul id="basket"></ul>
            Summe: <span id="totalprice"></span>
            <br />
            <div id="order"></div>
        </div>
        <hr/>
        <h3>Unsere Produkte:</h3>
        <table border="1">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Beschreibung</th>
                    <th>Preis</th>
                    <th><!--buttons--></th>
                </tr>
            </thead>
            <tbody id="articles" />
        </table>

        <script type='text/javascript'>

    /***********************************************************
    *      AJAX FUNCTIONALITY VIA DWR BELOW:
    *
    * Java classes on the server which can be invoked
    * via a javascript Proxy Object:
    *      Store,
    *      ShoppingBasket
    *
    * Java classes represented by beans can be invoked via javascript:
    *      Article,
    *      ShoppingBasket
    *
    * Skript parts:
    *      function    searchFormHandler()
    *      function    buyButtonHandler()
    *      var         articleRowConstruct[]
    *      function    displayArticles(articleList)
    *      function    displayShoppingBasket(basket)
    *
    * author: andre
    ***********************************************************/

    /*on page load we are loading the (empty) ShoppingBasket from
    the server and set the searchFormHandler onto the searchbutton.*/
    window.onload = function() {
        ShoppingBasket.getShoppingBasket(displayShopptingBasket);
        $("searchform").onsubmit = searchFormHandler;
    }

    /*being invoked when searchbutton is clicked.
    gets the search result from the server.
    when the response returns, function displayArticles
    will be called. (callback function)*/
    function searchFormHandler() {
        var searchexp = $("searchbox").value;
        Store.findArticles(searchexp, displayArticles);
        return false;
    }

    /*Handles a click on an Item's "kaufen" button
    the java method on the corresponding server class
    needs only the id of the item, the second argument
    is the function which should be invoked when the
    server-request returns back (callback function).*/
    function buyButtonHandler() {
        ShoppingBasket.buyArticle(this.articleId, displayShopptingBasket);
    }

    /*puts the items from the list into a table.
    being called from function searchFormSubmitHandler
    after it got the results from the server.
    */
    function displayArticles(articleListJSON) {
        DWRUtil.removeAllRows("articles");

        if (articleListJSON.length == 0) {
            alert("Keine passenden Produkte gefunden!");
        } else {
            DWRUtil.addRows("articles", articleListJSON, articleRowConstruct);
        }
    }

    /*used by DWRUtil to create a table element.
    dwrutil expects a value for each column.
    an array of functions being invoked for
    every line in the table.*/
    var articleRowConstruct = [ 
        function(article) {
            return article.name;
        }, 
        function(article) {
            return article.description;
        }, 
        function(article) {
            return article.formattedPrice;
        }, 
        function(article) {
            var butt = document.createElement("input");
            butt.setAttribute("type", "button");
            butt.setAttribute("value", "kaufen");
            butt.articleId = article.id;
            butt.onclick = buyButtonHandler;
            return butt;
        }
    ];

    /*displays the current shoppingBasket into the basket element,
    expects a list of strings. creates an unordered list and
    fills it with textnodes, one for each product.
    being called from function buyButtonHandler.*/
    function displayShopptingBasket(shoppingBasketJSON) {
        var basketUL = $("basket");
        basketUL.innerHTML = "";

        var contents = shoppingBasketJSON.simpleContents;

        for ( var elem in contents) {
            var text = contents[elem];

            /*an unordered list element*/
            var liElem = document.createElement("li");
            liElem.appendChild(document.createTextNode(text));
            basketUL.appendChild(liElem);
        }

        var totalPrice = $("totalprice");
        totalPrice.innerHTML = shoppingBasketJSON.formattedTotalPrice;

        var orderDiv = $("order");
        orderDiv.innerHTML = "";

        if (contents.length != 0) {
            var butt = document.createElement("input");
            butt.setAttribute("type", "button");
            butt.setAttribute("name", "bestellen");
            butt.setAttribute("value", "bestellen");
            butt.setAttribute("onclick",
                    "alert('Sie haben auf bestellen geklickt.');");
            orderDiv.appendChild(butt);
        }
    }
        </script>

    </body>
</html>
