package com.company;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.*;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;

public class Main {
    public static String serviceRoot = "https://cdatanorthwindsampleapiserver.azurewebsites.net/api.rsc/";

    public static final String authHeaderName = "x-cdata-authtoken";
    public static final String authHeaderValue = "XXXXXXXXXXXXX";

    public static final String ORDERS = "orders";
    public static final String ORDER_DETAILS = "order_details";

    public static void main(String[] args) {

        ClientEntitySetIterator<ClientEntitySet,ClientEntity> orderDetails = getClientEntitySetIterator(ORDER_DETAILS);

        while (orderDetails.hasNext()) {
            System.out.println("-------------------------------------------");

            ClientEntity orderDetail = orderDetails.next();
            ClientEntitySetIterator<ClientEntitySet,ClientEntity> order = getClientEntitySetIterator(ORDERS,
                    orderDetail.getProperty("order_id").getValue().toString());

            orderDetail.getProperties().forEach(x -> System.out.println(x.getName() + " : " + x.getValue() ));
            order.next().getProperties().forEach(x -> System.out.println(x.getName() + " : " + x.getValue() ));
        }
    }

    private static ClientEntitySetIterator<ClientEntitySet,ClientEntity> getClientEntitySetIterator(String entityName) {
        return getClientEntitySetIterator(entityName, null);
    }

    private static ClientEntitySetIterator<ClientEntitySet,ClientEntity> getClientEntitySetIterator(String entityName, String id) {
        ODataClient client = ODataClientFactory.getClient();
        RetrieveRequestFactory retrieveRequestFactory = client.getRetrieveRequestFactory();

        URIBuilder uri = client.newURIBuilder(serviceRoot).appendEntitySetSegment(entityName).top(10);

        if(id != null)
            uri.id(id);

        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> reqesut = retrieveRequestFactory.getEntitySetIteratorRequest(uri.build());
        reqesut.addCustomHeader(authHeaderName,authHeaderValue);

        ClientEntitySetIterator<ClientEntitySet,ClientEntity> body = reqesut.execute().getBody();
        return body;
    }
}
