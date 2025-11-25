-- Pegamos o estoque atual e a quantidade solicitada
local stock = tonumber(redis.call('GET', KEYS[1]))
local qty = tonumber(ARGV[1])

-- Verificamos se o estoque existe e é suficiente
if stock and stock >= qty then
	redis.call('DECRBY', KEYS[1] , ARGV[1])

	return 1 -- Sucesso
else
	return 0 -- Falha (sem estoque)
end