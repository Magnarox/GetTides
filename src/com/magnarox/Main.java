package com.magnarox;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("RUN");

        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = new GregorianCalendar(2012, 0, 1);

            HttpURLConnection con = prepareRequest(sdf.format(calendar.getTime()));
            List<Tide> extract = extractData(con, calendar);
            System.out.println(extract);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpURLConnection prepareRequest(String date) throws Exception {
        URL url = new URL("http://maree.info/160?d=" + date);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "maree.info");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        con.setRequestProperty("Accept-Language", "fr");

        return con;
    }

    public static List<Tide> extractData(HttpURLConnection con, Calendar calendar) throws Exception {
        List<Tide> extractedTides = new ArrayList<>();
        int status = con.getResponseCode();

        Document doc = Jsoup.parse(con.getInputStream(), "UTF-8", "http://maree.info");
        Element mareeJours = doc.getElementById("MareeJours");

        int i = 0;
        Element mareeJour = mareeJours.getElementById("MareeJours_0");
        while (mareeJour != null) {
            extractedTides.addAll(extractTides(mareeJour, calendar));
            i++;
            calendar.add(Calendar.DATE, 1);
            mareeJour = mareeJours.getElementById("MareeJours_" + i);
        }
        return extractedTides;
    }

    public static List<Tide> extractTides(Element jourElt, Calendar calendar) {
        List<Tide> extractedTides = new ArrayList<>();
        Elements elts = jourElt.getElementsByTag("td");

        List<TextNode> hoursElt = extractAllTextNodes(elts.get(0));
        List<TextNode> highElt = extractAllTextNodes(elts.get(1));
        List<TextNode> coeffElt = extractAllTextNodes(elts.get(2));

        for (int i = 0; i < hoursElt.size(); i++) {
            Tide tide = new Tide();
            String[] hs = hoursElt.get(i).text().split("h");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hs[0]));
            calendar.set(Calendar.MINUTE, Integer.valueOf(hs[1]));
            tide.setDate(calendar.getTime());

            tide.setHauteur(highElt.get(i).text());
            try {
                Double val = Double.valueOf(coeffElt.get(i).text());
                tide.setCoef(coeffElt.get(i).text());
                tide.setBassePleine("PM");
            } catch (NumberFormatException nfe) {
                tide.setBassePleine("BM");
            }

            extractedTides.add(tide);
        }

        return extractedTides;
    }

    public static List<TextNode> extractAllTextNodes(Element parent) {
        List<TextNode> textNodes = new ArrayList<>();

        for (Node child : parent.childNodes()) {
            if (child instanceof TextNode) {
                textNodes.add((TextNode) child);
            } else if (child.childNodeSize() > 0) {
                textNodes.addAll(extractAllTextNodes((Element) child));
            }
        }

        return textNodes;
    }
}
