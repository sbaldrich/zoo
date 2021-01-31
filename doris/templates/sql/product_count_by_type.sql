SELECT count(*) 
FROM warehouse.products 
WHERE product_type = '$PRODTYPE'
AND store_id=$STOREID