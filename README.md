# Extraction of csv files from Email and creation of xml with its elements containing those csv Files as base64 encoded Strings

Email Client (csv Files) --> SAP PI --> SAP ECC

1.  Read all the .csv file attachments from the incoming email
2.  Convert the .csv files to base64 encoded Strings
3.  Create the required xml document with base64 encoded data in its fields
4.  Write the xml file to the outputstream.
