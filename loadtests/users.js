import http from 'k6/http';
import { sleep, check } from 'k6';
import { Trend } from 'k6/metrics';

const cpuUsage = new Trend('cpu_usage_percent');
const memoryUsed = new Trend('jvm_memory_used_mb');

export const options = {
    insecureSkipTLSVerify: true,

    scenarios: {
        load: {
            executor: 'constant-vus',
            vus: 20,
            duration: '10s',
            exec: 'loadTest',
        },

        monitor: {
            executor: 'constant-vus',
            vus: 1,
            duration: '10s',
            exec: 'monitor',
        },
    },
};

export function setup() {

    const user = {
        username: 'loadtestuser',
        email: 'loadtest@loadtest.com',
        password: 'password',
    };

    const response = http.post(
        'https://localhost:8080/users/register',
        JSON.stringify(user),
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    check(response, {
        'test user created': (r) => r.status === 201,
    });

    return user;
}

export function loadTest(data) {

    const loginRequest = {
        username: data.username,
        password: data.password,
    };

    const response = http.post(
        'https://localhost:8080/users/login',
        JSON.stringify(loginRequest),
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    check(response, {
        'login successful': (r) => r.status === 200,
    });
}

export function monitor() {

    const cpuResponse = http.get(
        'https://localhost:8080/actuator/metrics/process.cpu.usage'
    );

    if (cpuResponse.status === 200) {
        const cpuData = JSON.parse(cpuResponse.body);
        const cpuValue = cpuData.measurements[0].value;

        cpuUsage.add(cpuValue * 100);
    }

    const memoryResponse = http.get(
        'https://localhost:8080/actuator/metrics/jvm.memory.used'
    );

    if (memoryResponse.status === 200) {
        const memoryData = JSON.parse(memoryResponse.body);
        const memoryValue = memoryData.measurements[0].value;

        memoryUsed.add(memoryValue / 1024 / 1024);
    }

    sleep(1);
}