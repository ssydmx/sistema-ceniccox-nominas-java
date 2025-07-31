CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `vw_altafondoahorro` AS
    SELECT 
        `r`.`IDRELLAB` AS `idrellab`,
        `gp`.`GRUPOPAGO` AS `grupopago`,
        `r`.`NUMEROEMPLEADO` AS `numeroempleado`,
        `e`.`APELLIDOPATERNO` AS `apellidopaterno`,
        `e`.`APELLIDOMATERNO` AS `apellidomaterno`,
        `e`.`NOMBRE` AS `nombre`,
        `cta`.`CLABE` AS `clabe`,
        `a`.`AREA` AS `area`,
        `b`.`NOMBRE` AS `banco`,
        `cta`.`CUENTA` AS `cuenta`,
        `r`.`IDGRUPOPAGO` AS `idgrupopago`,
        `r`.`FECHAINGRESO` AS `fechaingreso`
    FROM
        ((((((`rhrelacionlaboralcuentatb` `cta`
        JOIN `rhrelacionlaboraltb` `r` ON ((`cta`.`IDRELLAB` = `r`.`IDRELLAB`)))
        JOIN `nomgrupopagotb` `gp` ON ((`r`.`IDGRUPOPAGO` = `gp`.`IDGRUPOPAGO`)))
        JOIN `rhempleadotb` `e` ON ((`r`.`IDEMPLEADO` = `e`.`IDEMPLEADO`)))
        JOIN `rhrelacionlaboralposiciontb` `p` ON ((`r`.`IDRELACIONLABORALPOSICION` = `p`.`IDRELACIONLABORALPOSICION`)))
        JOIN `rhareatb` `a` ON ((`p`.`IDAREA` = `a`.`IDAREA`)))
        JOIN `catbancotb` `b` ON ((`cta`.`IDBANCO` = `b`.`IDBANCO`)))
    WHERE
        ((`r`.`ESTATUS` = '1')
            AND (`cta`.`ESTATUS` = '1')
            AND (`a`.`IDAREA` = 1))