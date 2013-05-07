%% convert a given heightmap into a tga picture file
function convert2tga(heightmap, filepath)
    % scale to 0..1
    hm = heightmap-min(heightmap(:));
    hm = hm / max(max(hm));
    imwrite(hm, filepath, 'bmp');
end