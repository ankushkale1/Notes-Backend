function pageInit() {
    getNoteBookMeta();
}

var current_notebook = null;
var current_note = null;

var notebook_meta_map = new Map();

var note_json = null;

var current_page = 0;
var per_page = 20;

var unsaved_content = false;

var NOTIFICATION_TYPE = {
    ALERT: "alert",
    WARNING: "warning",
    INFO: "info",
    ERROR: "error",
    SUCCESS: "success"
}

function checkUnsavedThings() {
    if (unsaved_content)
        if (confirm('Save Changes ??')) {
            addUpdateNote();
            unsaved_content = false;
        }
}

function getNote(noteid) {
    //console.log(editor.container.innerHTML);

    checkUnsavedThings();

    $.ajax({
        type: "GET",
        url: SERVER_URLS.GET_NOTE.replace('{noteid}', noteid),
        //url: "https://591dea16-659a-4324-88bf-2df285701d78.mock.pstmn.io/notes/getNote/14",
        success: function (res) {
            try {
                var data = JSON.parse(res.jsonnotes);
                //editor.setContents(data,'api');

                const parent = editor.root.parentElement;
                parent.removeChild(editor.root);
                editor.setContents(data, 'api');
                parent.appendChild(editor.root);
                showNotification('Note Loaded..', NOTIFICATION_TYPE.SUCCESS);

            } catch (e) {
                editor.container.firstChild.innerHTML = res.jsonnotes;
            }

            if (typeof res.jsonnotes == 'undefined') //so loading a blank object clears editor
                editor.container.firstChild.innerHTML = "";

            //var delta_ops = { "ops" : data.ops.slice(current_page*per_page,(current_page*per_page)+per_page) };
            //editor.setContents(new editor.delta(delta_ops),'api');

            //editor.container.firstChild.innerHTML="";
            //editor.container.firstChild.innerHTML = res.jsonnotes;

            $('#notename').text(res.notename);
            current_note = res;
            current_notebook = notebook_meta_map.get(res.notebook_id);
            $('#currentNotebook').text(current_notebook.notebookname);
            //note_json = JSON.parse(res);
            //editor.setContents(JSON.parse(res));
        },
        error: function (res, statuscode) {
            showNotification('Error while fetching Note..', NOTIFICATION_TYPE.ERROR);
            console.info(res + " " + statuscode);
        },
    });
}

//ntype alert, success, error, warning, info
function showNotification(ntext, ntype) {
    new Noty({
        type: ntype,
        text: ntext,
        theme: 'bootstrap-v4',
        timeout: 3000,
        progressBar: false
    }).show();
}

/*function getNextContent()
{
    var old_length = (current_page*per_page)+per_page;
    current_page++;

    var data = JSON.parse(current_note.jsonnotes);
    var start = (current_page*per_page)+1;
    var end = (current_page*per_page)+per_page;

    var delta_ops = { "ops" : data.ops.slice(start,end) };

    var delta = editor.getContents().retain(old_length).concat(delta_ops);
    editor.updateContents(delta);
}*/

function getNoteBookMeta() {
    $.ajax({
        type: "GET",
        url: SERVER_URLS.GET_NOTEBOOKS,
        success: function (res) {
            for (i = 0; i < res.length; i++) {
                notebook_meta_map.set(res[i].notebook_id, res[i]);
            }

            populateNoteBooks();

            showNotification('Loaded Notebooks..', NOTIFICATION_TYPE.INFO);
        },
        error: function (res, statuscode) {
            console.info(res + " " + statuscode);
            showNotification('Error while fetching Notebooks..', NOTIFICATION_TYPE.ERROR);
        },
    });
}

function addNotebook(notebookname, notebook) {
    book = {
        "notebook_id": null,
        "notebookname": notebookname,
        "parent": {
            "notebook_id": notebook
        }
    }

    $.ajax({
        type: "POST",
        url: SERVER_URLS.ADD_NOTEBOOK,
        data: JSON.stringify(book),
        dataType: "json",
        contentType: "application/json; charset=utf-8",
        success: function (res) {
            notebook_meta_map.set(res.notebook_id, res);
            populateNoteBooks();
            $.magnificPopup.close();
            showNotification('Added new Notebook', NOTIFICATION_TYPE.SUCCESS);
        },
        error: function (res, statuscode) {
            console.info(res + " " + statuscode);
            showNotification('Error while adding a new Notebook', NOTIFICATION_TYPE.ERROR);
        },
    });
}

function getPlainContent() {
    var html = editor.container.innerHTML;
    html = html.replace(/<style([\s\S]*?)<\/style>/gi, '');
    html = html.replace(/<script([\s\S]*?)<\/script>/gi, '');
    html = html.replace(/<\/div>/ig, '\n');
    html = html.replace(/<\/li>/ig, '\n');
    html = html.replace(/<li>/ig, '  *  ');
    html = html.replace(/<\/ul>/ig, '\n');
    html = html.replace(/<\/p>/ig, '\n');
    html = html.replace(/<br\s*[\/]?>/gi, "\n");
    html = html.replace(/<[^>]+>/ig, '');
    html = html.replace(/\n+/g, '\n')

    return html;
}

function addUpdateNote(notename, notebook) {
    //console.log(editor.container.innerHTML);

    var note = null;

    if (typeof notename == 'undefined') //update existing
    {
        note = {
            "note_id": current_note.note_id,
            "notename": current_note.notename,
            "jsonnotes": JSON.stringify(editor.getContents()),
            "plain_content": getPlainContent(),
            "keywords": current_note.keywords,
            "notebook": {
                "notebook_id": current_note.notebook_id
            }
        }

        $.ajax({
            type: "POST",
            url: SERVER_URLS.ADD_NOTE,
            data: JSON.stringify(note),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function (res) {
                //editor.container.innerHTML = res.jsonnotes;
                showNotification('Updated Note', NOTIFICATION_TYPE.SUCCESS);
            },
            error: function (res, statuscode) {
                console.info(res + " " + statuscode);
                showNotification('Error while updating a note', NOTIFICATION_TYPE.ERROR);
            },
        });
    } else {
        checkUnsavedThings();

        note = {
            "note_id": null,
            "notename": notename,
            "jsonnotes": "Hello World",
            "keywords": ['Test'],
            "notebook": {
                "notebook_id": notebook
            }
        }

        $.ajax({
            type: "POST",
            url: SERVER_URLS.ADD_NOTE,
            data: JSON.stringify(note),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function (res) {
                notebook_meta_map.get(res.notebook_id).notes.push(res);
                current_note = res;
                current_notebook = notebook_meta_map.get(res.notebook_id);
                editor.container.firstChild.innerHTML = "";
                //editor.setContents("",'api');
                $('#notename').text(res.notename);
                $('#currentNotebook').text(current_notebook.notebookname);
                $.magnificPopup.close();
                showNotification('Added new blank Note', NOTIFICATION_TYPE.SUCCESS);
            },
            error: function (res, statuscode) {
                console.info(res + " " + statuscode);
                showNotification('Error while adding Note', NOTIFICATION_TYPE.ERROR);
            },
        });

        populateNoteBooks();
    }
}

function deleteNotebook(notebookid) {
    if (confirm('Delete Notebook ??')) {
        $.ajax({
            type: "GET",
            url: SERVER_URLS.DELETE_NOTEBOOK + "/" + notebookid,
            success: function (res) {
                notebook_meta_map.delete(notebookid);
                populateNoteBooks();
                showNotification('Deleted Notebook', NOTIFICATION_TYPE.SUCCESS);
            },
            error: function (res, statuscode) {
                console.info(res + " " + statuscode);
                showNotification('Delete Notebook failed..', NOTIFICATION_TYPE.ERROR);
            },
        });
    }
}

function deleteNote(noteid) {
    if (confirm('Delete Note ??')) {
        $.ajax({
            type: "GET",
            url: SERVER_URLS.DELETE_NOTE + "/" + noteid,
            success: function (res) {
                getNoteBookMeta();
                showNotification('Deleted Note', NOTIFICATION_TYPE.SUCCESS);
            },
            error: function (res, statuscode) {
                console.info(res + " " + statuscode);
                showNotification('Delete Note failed..', NOTIFICATION_TYPE.ERROR);
            },
        });
    }
}


function searchText() {
    $.ajax({
        type: "GET",
        url: SERVER_URLS.SEARCH.replace('{searchtxt}', $("[name='stxt']").val()),
        success: function (res) {
            $('#search-content').html("");

            for (i = 0; i < res.length; i++) {
                note = res[i];
                //var content = note.plain_content.replaceAll(stext,"<mark>"+stext+"</mark>");
                //console.info(note.notename);
                $('#search-content').append(`

                <div class="card" style="max-height: 200px; overflow: hidden; margin: 10px;">
                    <div class="card-header">
                        <a style="font-size:20px;" data-toggle="collapse" href="#scontent_${note.note_id}" aria-expanded="true" aria-controls="scontent_${note.note_id}">
                            ${note.notename}
                        </a>
                        <span style="cursor: pointer; font-size:20px;" class="fa fa-large-op fa-arrow-circle-o-right" onclick="getNote(${note.note_id})"></span>
                    </div>
                    <div id="scontent_${note.note_id}" class="collapse">
                        <div class="card-block" style="cursor: pointer;" onclick="$.magnificPopup.close();getNote(${note.note_id})">
                            <div class="nid" style='display:none;'>${note.note_id}</div>
                            <div class="nbody">${note.plain_content}</div>
                        </div>
                    </div>
                </div>
                `);
            }
        },
        error: function (res, statuscode) {
            console.info(res + " " + statuscode);
            showNotification('Searching Failed..', NOTIFICATION_TYPE.ERROR);
        },
    });
}

const dSearch = function (fn, delay) {
    let timer;

    return function () {
        let context = this;
        args = arguments;
        clearTimeout(timer);

        timer = setTimeout(() => {
            fn.apply(context, args);
        }, delay);
    }
}

const search = dSearch(searchText, 300);
