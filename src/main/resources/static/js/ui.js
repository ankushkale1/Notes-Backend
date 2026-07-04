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

// Function to resize a base64 image
function resizeImage(base64Str, scale) {
    return new Promise((resolve) => {
        const img = new Image();
        img.src = base64Str;
        img.onload = () => {
            const canvas = document.createElement('canvas');
            const width = img.width * scale;
            const height = img.height * scale;
            canvas.width = width;
            canvas.height = height;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0, width, height);
            resolve(canvas.toDataURL());
        };
    });
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

        // Fix for jumping to top on paste
        const mainContainer = document.querySelector('.main');
        editor.root.addEventListener('paste', function(e) {
            if (!mainContainer) return;
            
            const currentScrollTop = mainContainer.scrollTop;
            setTimeout(() => {
                if (mainContainer.scrollTop !== currentScrollTop) {
                    mainContainer.scrollTop = currentScrollTop;
                }
            }, 0);
            setTimeout(() => {
                if (mainContainer.scrollTop !== currentScrollTop) {
                    mainContainer.scrollTop = currentScrollTop;
                }
            }, 50);
        });

        // Image resizing logic
        let selectedImage = null;
        let originalBase64 = '';
        const resizeToolbar = document.getElementById('image-resize-toolbar');

        editor.root.addEventListener('click', function(e) {
            if (e.target && e.target.tagName === 'IMG') {
                if (selectedImage) {
                    selectedImage.classList.remove('selected-image');
                }
                selectedImage = e.target;
                originalBase64 = selectedImage.src; // Store original source
                selectedImage.classList.add('selected-image');

                const imgRect = selectedImage.getBoundingClientRect();
                const mainRect = mainContainer.getBoundingClientRect();
                
                const top = imgRect.top - mainRect.top + mainContainer.scrollTop - 40;
                const left = imgRect.left - mainRect.left + (imgRect.width / 2) - (resizeToolbar.offsetWidth / 2);

                resizeToolbar.style.top = `${Math.max(10, top)}px`;
                resizeToolbar.style.left = `${Math.max(10, left)}px`;
                resizeToolbar.style.display = 'flex';
                
                e.stopPropagation();
            }
        });

        resizeToolbar.addEventListener('click', async function(e) {
            if (e.target && e.target.tagName === 'BUTTON' && selectedImage) {
                e.stopPropagation();
                const scale = e.target.getAttribute('data-scale');
                const blot = Quill.find(selectedImage);
                const index = editor.getIndex(blot);

                showLoader('Resizing image...');

                if (scale === '100') {
                    // Replace current image with the original
                    editor.deleteText(index, 1);
                    editor.insertEmbed(index, 'image', originalBase64);
                } else {
                    // Resize and replace
                    const newBase64 = await resizeImage(originalBase64, parseFloat(scale) / 100);
                    editor.deleteText(index, 1);
                    editor.insertEmbed(index, 'image', newBase64);
                }
                
                // The new image is now selected, so we need to re-query it
                setTimeout(() => {
                    const newBlot = editor.getLeaf(index)[0];
                    if (newBlot && newBlot.domNode.tagName === 'IMG') {
                        if (selectedImage) selectedImage.classList.remove('selected-image');
                        selectedImage = newBlot.domNode;
                        selectedImage.classList.add('selected-image');
                    }
                    hideLoader();
                }, 100);

                unsaved_content = true;
            }
        });

        document.addEventListener('click', function(e) {
            if (resizeToolbar.style.display === 'flex' && !resizeToolbar.contains(e.target)) {
                resizeToolbar.style.display = 'none';
                if (selectedImage) {
                    selectedImage.classList.remove('selected-image');
                    selectedImage = null;
                }
            }
        });

    } catch (error) {
        console.error("[Error] Failed to initialize Quill editor:", error);
    }
});

// ... rest of the file is unchanged ...

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