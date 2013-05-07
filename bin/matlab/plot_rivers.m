%% test river plotting / creating

path = '../../';
file = 'test_plotriver.txt';

filepath = [path file];

% read file
fid = fopen(filepath);
text = textscan(fid,'%s%*[^\n]');
text = text{1};
rivers = cell(length(text),1);
for line = 1:length(text)
    rivers{line} = regexp(text{line}, ',', 'split');
end
fclose(fid);

% create plottable data array
riverdata = cell(length(rivers), 4);
color = {'k-','b-','r-','y-'};
figure(2)
for iRiver = 1:length(rivers)
    vertices = rivers{iRiver};
    vdata = [];
    for iv = 1:length(vertices)
        data = textscan(vertices{iv},'%f:%f:%f#%f');
        vdata(1,end+1) = data{1};
        vdata(2,end) = data{2};
        vdata(3,end) = data{3};
        vdata(4,end) = data{4};
    end
    riverdata(iRiver,1) = {vdata(1,:)};
    riverdata(iRiver,2) = {vdata(2,:)};
    riverdata(iRiver,3) = {vdata(3,:)};
    riverdata(iRiver,4) = {vdata(4,:)};
    
    plot(vdata(1,:), vdata(2,:),color{iRiver})
    hold on
end
hold off
axis equal
