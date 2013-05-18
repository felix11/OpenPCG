%% test perlin noise by plotting it

path = '../../';
file = 'perlin_noise_grid2d_test.txt';
fileheight = 'test_heightmap.txt';
fileheight_base = 'test_heightmap';
fileres = 'test_resmap.txt';
filewaters = 'test_watershedmap.txt';
filesoil = 'test_soilmap.txt';
filecities = 'test_citymap.txt';
fileroads = 'test_roads.txt';
filepopulationdensity = 'test_populationdensitymap.txt';
filepopdensity_base = 'test_populationdensitymap';
fileforest1 = 'test_forestmap1.txt';

mapheights = readmap(path, fileheight);
mapresources = readmap(path, fileres);
mapw = readmap(path, filewaters);
maps = readmap(path, filesoil);
mapc = readmap(path, filecities);
maproads = readmap(path, fileroads);
mappd = readmap(path, filepopulationdensity);
mapf1 = readmap(path, fileforest1);

layer2 = readlayer(path, fileheight_base, 4, 4); % two rows and cols
mapheights = combinelayer(layer2);

layerpd2 = readlayer(path, filepopdensity_base, 2, 2); % two rows and cols
mappd = combinelayer(layerpd2);

%convert2tga(maph,[path 'test_heightmap.bmp']);

% combine cities in one map, dont use population count
mapc(mapc > 0) = 1;
mapc(mapc < 1) = 0;

% scale forest, resources and cities maps to the same dimensions as the hm
s = size(mapheights);
mapresources = imresize(mapresources,s);
mapc = imresize(mapc,s,'nearest');
mapf1 = imresize(mapf1,s);
mappd = imresize(mappd,s);

% scale the map to be able to see its features
factor = 30;
zcolormin = -factor/30*9;
zcolormax = factor;

figure(1)
subplot(1,2,1)
mapheights = ((mapheights))*factor;
mapheights(mapc > 0) = max(max(mapheights));
%map = imread('real_island1.tif');
%map = imread('canyon_1.tif');
%map = imread('real_island2.tif');
%map = double(map);
%map = map / max(max(map)) * 20000 / 512;

% plot heightmap first
imagesc(mapheights)
zlimits = [zcolormin zcolormax];
demcmap(zlimits,255);
%colormap(gray)
shading flat
axis image
axis off
box off

colorbar

% blend resources
hold on
contour(mapresources*200,10)
%zlimits = [min(mapr(:)) max(mapr(:))];
%demcmap(zlimits,64);
%shading interp
hold off

% blend watersheds
hold on
%vals = unique(mapw(:));
% counter = 1;
% for iv = 1:length(vals)
%     v = vals(iv);
%     if(v > 0)
%         mapw(mapw == v) = counter;
%         counter = counter + 1;
%     end
% end
%contour(mapw/20,10)
%colormap(gray)
%zlimits = [min(mapw(:)) max(mapw(:))];
%demcmap(zlimits,64);
hold off

figure(1)
%mappd(maproads > 0) = max(max(mappd));
subplot(1,2,2)
imagesc(mappd)
hold on
contour(mapheights)
hold off
shading interp
axis image
colorbar

return
figure(3)
imagesc(maproads)
shading interp
axis image
colorbar

figure(4)
% plot heightmap first
%imagesc(maph)
zlimits = [min(mapheights(:)) max(mapheights(:))];
%demcmap(zlimits,64);
%shading interp

% blend soil quality
surf(maps*30)
colormap(gray)
colorbar
 set(gca,'YDir','Reverse')

%zlimits = [min(mapr(:)) max(mapr(:))];
%demcmap(zlimits,64);
shading interp
axis image

figure(1)
return
figure(1)
hold on
% plot heightmap first
contour(mapf1,2)
colorbar
%axis image
hold off