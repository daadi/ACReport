function storeApiKey( apiKey )
{
	var date = new Date();
	date.setTime(date.getTime()+(14*24*60*60*1000));
	$.cookies.set("apikey", apiKey, { expiresAt : date, path : "/" });
}

var apiKey = $.cookies.get("apikey"); 
if (apiKey)
{
	storeApiKey(apiKey);
}