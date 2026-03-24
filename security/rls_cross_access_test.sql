-- RLS cross-access smoke test for Supabase/PostgreSQL
-- Objetivo: validar que un usuario no puede leer/escribir filas de otro.
-- Requiere ejecutar con un rol con permisos para SET ROLE/claims en entorno de pruebas.

begin;

-- Ajusta estos UUID a usuarios reales de testing.
-- Usuario A
select set_config('request.jwt.claim.sub', '00000000-0000-0000-0000-0000000000aa', true);

-- Debe insertar solo sus propias filas
insert into public.cuentas (user_id, usuario, password, categoria)
values ('00000000-0000-0000-0000-0000000000aa', 'v2:dummy', 'v2:dummy', 'Otros');

-- Usuario B
select set_config('request.jwt.claim.sub', '00000000-0000-0000-0000-0000000000bb', true);

-- 1) B no debería ver filas de A
-- Esperado: 0
select count(*) as should_be_zero
from public.cuentas
where user_id = '00000000-0000-0000-0000-0000000000aa';

-- 2) B no debería borrar filas de A
-- Esperado: 0 rows affected
delete from public.cuentas
where user_id = '00000000-0000-0000-0000-0000000000aa';

rollback;
