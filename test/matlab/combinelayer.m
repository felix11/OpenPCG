%% combines the individual maps of a layer into one
function map = combinelayer(layer)
    minimapRows = size(layer{1,1},1);
    minimapCols = size(layer{1,1},2);
    map = zeros(size(layer,1) * minimapRows, size(layer,2) * minimapCols);
    
    for row = 1:size(layer,1)
        for col = 1:size(layer,2)
            map((row-1)*minimapRows+1:row*minimapRows, ...
                (col-1)*minimapRows+1:col*minimapCols) = layer{row,col};
        end
    end
    
    %map = map(end:-1:1,:);
end