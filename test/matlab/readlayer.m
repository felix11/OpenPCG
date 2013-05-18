function layer = readlayer(path, filebase, rows, cols)
    layer = cell(rows, cols);
    for row = 0:rows-1
        for col = 0:cols-1
            layer{row+1,col+1} = csvread([path filebase '_' num2str(row) '_' num2str(col) '.txt']);
        end
    end
end