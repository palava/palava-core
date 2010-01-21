function count(src, dest)
{
    len = document.getElementById(src).value.length;
    d = document.getElementById(dest);
    if (d.id == 'jslength')
        d.innerHTML = len;
    else
        d.value = len;
}


function insertText(dest, text)
{
    textfield = document.getElementById(dest);
    start = textfield.selectionStart;

    textfield.value = textfield.value.substring(0, start) + text + textfield.value.substr(start);
}
