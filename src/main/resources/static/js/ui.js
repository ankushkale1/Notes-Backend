$(document).ready(function () {
    // sideBarInit();
    pageInit();
    popup_init();

    $('.ql-editor').keypress(function () {
        unsaved_content = true;
    });

    //so that if we accedently close it will prompt
    window.addEventListener("beforeunload", function (e) {
        var confirmationMessage = "\o/";

        (e || window.event).returnValue = confirmationMessage; //Gecko + IE
        return confirmationMessage;                            //Webkit, Safari, Chrome
    });
});

// Utility functions for Loader
function showLoader(message = "Loading...") {
    $('#loader-text').text(message);
    $('#app-loader').css('display', 'flex');
}

function hideLoader() {
    $('#app-loader').fadeOut(200);
}

function clearPrevSearch() {
    $('#search-content').html('');
    $('[name="stxt"]').val("");
}

/*function htmlbodyHeightUpdate() {
    var height3 = $(window).height()
    var height1 = $('.nav').height() + 50
    height2 = $('.main').height()
    if (height2 > height3) {
        $('html').height(Math.max(height1, height3, height2) + 10);
        $('body').height(Math.max(height1, height3, height2) + 10);
    } else {
        $('html').height(Math.max(height1, height3, height2));
        $('body').height(Math.max(height1, height3, height2));
    }
}

function sideBarInit() {
    htmlbodyHeightUpdate()
    $(window).resize(function () {
        htmlbodyHeightUpdate()
    });
    $(window).scroll(function () {
        height2 = $('.main').height()
        htmlbodyHeightUpdate()
    });
}*/

function popup_init() {
    $(document).ready(function () {
        $('.mypopup').magnificPopup({
            type: 'inline',
            midClick: true
        });
    });
}

hljs.configure({
    languages: ['java']
});

var toolbarOptions = [
    ['bold', 'italic', 'underline', 'code-block'],
    ['link', 'image'],
    [{ 'header': 1 }, { 'header': 2 }],
    [{ 'list': 'ordered' }, { 'list': 'bullet' }],
    [{ 'indent': '-1' }, { 'indent': '+1' }],
    [{ 'size': ['small', false, 'large', 'huge'] }],
    [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
    [{ 'color': [] }, { 'background': [] }],
    [{ 'font': [] }],
    [{ 'align': [] }],
    ['clean']
];

var editor = null;

var bindings = {
    code: {
        key: 'C',
        shiftKey: null,
        ctrlKey: true,
        altKey: true,
        handler: function (range, context) {
            editor.formatText(range, 'code', true);
        }
    }
};

function imageHandler() {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();
    input.onchange = () => {
        const file = input.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const base64Image = e.target.result;
                const range = editor.getSelection();
                editor.insertEmbed(range.index, 'image', base64Image);
            };
            reader.readAsDataURL(file);
        }
    };
}

$(window).on('load', function () {
    if (typeof hljs === 'undefined') {
        console.error("[Error] Highlight.js is not loaded! Syntax highlighting will fail.");
        return;
    }
    window.hljs = hljs;

    try {
        editor = new Quill('#editor', {
            theme: 'snow',
            readOnly: false,
            imageDrop: true,
            modules: {
                syntax: {
                    highlight: function(text) {
                        try {
                            let result = hljs.highlightAuto(text, ['java', 'javascript', 'xml', 'css']);
                            if (result.relevance < 2) {
                                return hljs.highlight(text, { language: 'java' }).value;
                            }
                            return result.value;
                        } catch (e) {
                            console.warn("[Quill Syntax] Highlighting failed, falling back to raw text.", e);
                            return text;
                        }
                    }
                },
                toolbar: {
                    container: toolbarOptions,
                    handlers: {
                        image: imageHandler
                    }
                },
                magicUrl: true,
                keyboard: {
                    bindings: bindings
                }
            }
        });
        console.log("[Debug] Quill Editor initialized successfully with Java-biased syntax highlighting.");
    } catch (error) {
        console.error("[Error] Failed to initialize Quill editor:", error);
    }
});

// UPDATED TEMPLATE: Modern flexbox layout matching the dark mode CSS
var menu_template = `
    <li data-toggle="collapse" data-target="#nb_{notebook_id}" class="collapsed folder-item">
        <a>
            <div class="menu-item-content">
                <i class="fa fa-folder-open"></i>
                <span>{notebook_name}</span>
            </div>
            <i onclick="deleteNotebook({notebook_id}); event.stopPropagation();" class="fa fa-trash btn" style="padding: 0; background: transparent; color: var(--text-muted); border: none;"></i>
        </a>
    </li>
    <ul class="sub-menu collapse" id="nb_{notebook_id}">
        {menuitems}
    </ul>
`;

// UPDATED TEMPLATE: Note child items with modern flexbox
var menu_item_template = `
    <li onclick="$('.sub-menu li').removeClass('active'); $(this).addClass('active'); getNote({note_id});">
        <a id="n_{note_id}">
            <div class="menu-item-content">
                <i class="fa fa-file-lines"></i>
                <span>{note_name}</span>
            </div>
            <i onclick="deleteNote({note_id}); event.stopPropagation();" class="fa fa-trash btn" style="padding: 0; background: transparent; color: var(--text-muted); border: none;"></i>
        </a>
    </li>
`;

var mymenu = "";

$(document).on('click', function (event) {
    const $sidebar = $('.modern-sidebar');
    const $toggleBtn = $('#topbarToggle');

    // Check if sidebar is currently visible (body does NOT have the collapsed class)
    const isSidebarOpen = !$('body').hasClass('sidebar-collapsed');

    // If sidebar is open, and the click was NOT on the sidebar or the toggle button
    if (isSidebarOpen &&
        !$sidebar.is(event.target) &&
        $sidebar.has(event.target).length === 0 &&
        !$toggleBtn.is(event.target) &&
        $toggleBtn.has(event.target).length === 0) {

        $('body').addClass('sidebar-collapsed');
    }
});

function populateNoteBooks() {
    $('#menu-content').html("");
    $('.mynotebooks').html('');

    for (let value of notebook_meta_map.values()) {
        var notebook = value;

        var menu = menu_template;
        menu = menu.replaceAll("{notebook_id}", notebook.notebook_id);
        menu = menu.replaceAll("{notebook_name}", notebook.notebookname);

        var menu_items = "";

        //populate create note select
        $('.mynotebooks').append(`<option value="${notebook.notebook_id}"> 
                                ${notebook.notebookname} 
                            </option>`);

        if (notebook.notes) {
            for (var j = 0; j < notebook.notes.length; j++) {
                var note = notebook.notes[j];
                var menu_item = menu_item_template;
                menu_item = menu_item.replaceAll("{note_id}", note.note_id);
                menu_item = menu_item.replaceAll("{note_name}", note.notename);
                menu_item = menu_items += "\n " + menu_item;
            }

            menu = menu.replaceAll("{menuitems}", menu_items);
        }

        $('#menu-content').append(menu);
        mymenu += "\n " + menu;
    }
}

String.prototype.replaceAll = function (stringToFind, stringToReplace) {
    if (stringToFind === stringToReplace) return this;
    var temp = this;
    var index = temp.indexOf(stringToFind);
    while (index != -1) {
        temp = temp.replace(stringToFind, stringToReplace);
        index = temp.indexOf(stringToFind);
    }
    return temp;
};