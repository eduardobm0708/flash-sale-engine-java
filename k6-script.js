import http from 'k6/http';
import { check } from 'k6';

export const options = {
    // Vamos começar devagar para não travar seu PC, depois aumentamos
    vus: 100, // 100 Usuários simultâneos
    duration: '30s', // Por 30 segundos
};

export default function () {
    const url = 'http://192.168.0.141:8080/reserve';

    const payload = JSON.stringify({
        userId: 'user_' + __VU, // Gera um ID único por usuário virtual
        itemId: 'iphone_15',
        quantity: 1,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    // Verificamos se o sistema respondeu (seja sucesso 202 ou falha 409)
    check(res, {
        'status é 202 ou 409': (r) => r.status === 202 || r.status === 409,
    });
}