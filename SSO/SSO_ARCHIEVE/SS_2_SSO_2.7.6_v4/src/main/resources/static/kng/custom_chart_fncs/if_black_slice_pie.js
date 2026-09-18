function if_black_slice_pie(color)
{
    if (!color || typeof color !== "string") return false;

    if (color.charAt(0) === "#") {
        color = color.substring(1);
    }

    if (color.length !== 6) return false;

    var r = parseInt(color.substring(0, 2), 16);
    var g = parseInt(color.substring(2, 4), 16);
    var b = parseInt(color.substring(4, 6), 16);

    // Perceived brightness
    var brightness = (r * 299 + g * 587 + b * 114) / 1000;

    return brightness < 128; // dark color
}