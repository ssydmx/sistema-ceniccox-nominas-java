CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `vw_ausentismosua` AS
    (SELECT 
        `inc`.`IDINCIDENCIA` AS `idincidencia`,
        `inc`.`UNIDADES` AS `tiempo`,
        `inc`.`REFERENCIA01` AS `referenciaa`,
        `inc`.`REFERENCIA02` AS `referenciab`,
        DATE_FORMAT(`inc`.`FECHAAUX01`, '%d/%m/%Y') AS `fecharegistro`,
        DATE_FORMAT(`inc`.`FECHAAUX01`, '%c') AS `mesregistro`,
        DATE_FORMAT(`inc`.`FECHAAUX01`, '%Y') AS `anioregistro`,
        DATE_FORMAT(`inc`.`FECHAAUX02`, '%d/%m/%Y') AS `fecharegistroimss`,
        DATE_FORMAT(`inc`.`FECHAAUX02`, '%c') AS `mesregistroimss`,
        DATE_FORMAT(`inc`.`FECHAAUX02`, '%Y') AS `anioregistroimss`,
        `con`.`CONCEPTO` AS `concepto`,
        `con`.`NOMBRE` AS `nombreconcepto`,
        `con`.`FALTA` AS `falta`,
        `con`.`INCAPACIDAD` AS `incapacidad`,
        (SELECT 
                `gp`.`GRUPOPAGO`
            FROM
                (`nomgrupopagotb` `gp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`gp`.`IDGRUPOPAGO` = `rl`.`IDGRUPOPAGO`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `grupopago`,
        (SELECT 
                `gp`.`NOMBRE`
            FROM
                (`nomgrupopagotb` `gp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`gp`.`IDGRUPOPAGO` = `rl`.`IDGRUPOPAGO`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `nombregrupopago`,
        (SELECT 
                DATE_FORMAT(`emp`.`FECHANACIMIENTO`, '%d/%m/%Y')
            FROM
                (`rhempleadotb` `emp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`emp`.`IDEMPLEADO` = `rl`.`IDEMPLEADO`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `fechanacimiento`,
        (SELECT 
                `rl`.`NUMEROEMPLEADO`
            FROM
                `rhrelacionlaboraltb` `rl`
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `numeroempleado`,
        (SELECT 
                CONCAT(IFNULL(`emp`.`APELLIDOPATERNO`, ''),
                            ' ',
                            IFNULL(`emp`.`APELLIDOMATERNO`, ''),
                            ' ',
                            `emp`.`NOMBRE`)
            FROM
                (`rhempleadotb` `emp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`emp`.`IDEMPLEADO` = `rl`.`IDEMPLEADO`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `nombreempleado`,
        (SELECT 
                `emp`.`RFC`
            FROM
                (`rhempleadotb` `emp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`emp`.`IDEMPLEADO` = `rl`.`IDEMPLEADO`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `rfc`,
        (SELECT 
                `emp`.`CURP`
            FROM
                (`rhempleadotb` `emp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`emp`.`IDEMPLEADO` = `rl`.`IDEMPLEADO`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `curp`,
        (SELECT 
                `emp`.`AFILIACION`
            FROM
                (`rhempleadotb` `emp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`emp`.`IDEMPLEADO` = `rl`.`IDEMPLEADO`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `nss`,
        (SELECT 
                `rp`.`IDREGISTROPATRONAL`
            FROM
                (`admregistropatronaltb` `rp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`rp`.`IDREGISTROPATRONAL` = `rl`.`IDREGISTROPATRONAL`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `idregistropatronal`,
        (SELECT 
                `rp`.`REGISTROPATRONAL`
            FROM
                (`admregistropatronaltb` `rp`
                JOIN `rhrelacionlaboraltb` `rl` ON ((`rp`.`IDREGISTROPATRONAL` = `rl`.`IDREGISTROPATRONAL`)))
            WHERE
                (`rl`.`IDRELLAB` = `inc`.`IDRELLAB`)) AS `registropatronal`,
        (SELECT 
                `p`.`PUESTO`
            FROM
                ((`admpuestotb` `p`
                JOIN `rhrelacionlaboralposiciontb` `pos` ON ((`p`.`IDPUESTO` = `pos`.`IDPUESTO`)))
                JOIN `rhrelacionlaboraltb` `rl` ON ((`pos`.`IDRELACIONLABORALPOSICION` = `rl`.`IDRELACIONLABORALPOSICION`)))
            WHERE
                (`inc`.`IDRELLAB` = `rl`.`IDRELLAB`)) AS `puesto`,
        (SELECT 
                `p`.`NOMBRE`
            FROM
                ((`admpuestotb` `p`
                JOIN `rhrelacionlaboralposiciontb` `pos` ON ((`p`.`IDPUESTO` = `pos`.`IDPUESTO`)))
                JOIN `rhrelacionlaboraltb` `rl` ON ((`pos`.`IDRELACIONLABORALPOSICION` = `rl`.`IDRELACIONLABORALPOSICION`)))
            WHERE
                (`inc`.`IDRELLAB` = `rl`.`IDRELLAB`)) AS `nombrepuesto`
    FROM
        (`nomincidenciastb` `inc`
        JOIN `nomconceptostb` `con` ON ((`inc`.`IDCONCEPTO` = `con`.`IDCONCEPTO`)))
    WHERE
        ((`con`.`FALTA` = 1)
            OR (`con`.`INCAPACIDAD` = 1)))