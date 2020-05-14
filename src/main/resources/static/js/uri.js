const SERVER_BASE = "http://localhost:8080/notes/"

const SERVER_URLS = 
{
    "GET_NOTEBOOKS" : SERVER_BASE+"getNoteBooks",
    "SEARCH" : SERVER_BASE+"search/{searchtxt}",
    "GET_NOTE" : SERVER_BASE+"getNote/{noteid}",
    "ADD_NOTE" : SERVER_BASE+"addNote",
    "ADD_NOTEBOOK" : SERVER_BASE+"addNotebook",
    "DELETE_NOTE" : SERVER_BASE+"deleteNote",
    "DELETE_NOTEBOOK" : SERVER_BASE+"deleteNotebook",
    "EXPORT_DB" : SERVER_BASE+"/",
    "IMPORT_DB" : SERVER_BASE+"/",
    "EXPORT_PDF" : SERVER_BASE+"/",
    "EXPORT_JSON" : SERVER_BASE+"/",
    "PRINT" : ""
};