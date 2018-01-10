# SAP PI java Mapping with Attachments - 2
SAP PI Java Mapping - Extract csv files from the mail and attach them to xml file as base64 encoded files

1.  Read all the .csv file attachments from the incoming email
2.  Convert the .csv files to base64 encoded Strings
3.  Create the required xml document with base64 encoded data in its fields
4.  Write the xml file to the outputstream.
