import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  //vus: 150,
	vus: 500,
	duration: '10s',
};

export default function () {
	
	const url = (__ENV.REACTIVE === 'true') ? `http://username:password@host.docker.internal:8080/clientes`: 'http://host.docker.internal:8081/clientes';
	const res = http.get(url);
	
	check(res, {
	    'status is 200': (r) => r.status === 200
	});
	
	//think time
	sleep(1);
}