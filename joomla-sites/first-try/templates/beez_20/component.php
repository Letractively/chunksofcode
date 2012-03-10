<!-- *********** debug:START of file:   templates/beez_20/component.php   -->

<?php

    /**
     * @package                Joomla.Site
     * @subpackage    Templates.beez_20
     * @copyright        Copyright (C) 2005 - 2012 Open Source Matters, Inc. All rights reserved.
     * @license                GNU General Public License version 2 or later; see LICENSE.txt
     */

    // No direct access.
    defined('_JEXEC') or die;

    $color = $this->params->get('templatecolor');

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php echo $this->language; ?>" lang="<?php echo $this->language; ?>" dir="<?php echo $this->direction; ?>">
<head>

    <!-- *********** debug:INCLUDE_START type="head"   in file: templates/breez_20/component.php -->
    <jdoc:include type="head"  />
    <!-- *********** debug:INCLUDE_END type="head"   in file: templates/breez_20/component.php -->

    <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/system/css/system.css" type="text/css" />
    <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template; ?>/css/template.css" type="text/css" />
    <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template; ?>/css/position.css" type="text/css" />
    <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template; ?>/css/layout.css" type="text/css" media="screen,projection" />
    <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template; ?>/css/print.css" type="text/css" media="Print" />
    <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template; ?>/css/<?php echo $color; ?>.css" type="text/css" />

<?php

    $files = JHtml::_('stylesheet', 'templates/'.$this->template.'/css/general.css', null, false, true);

    if ($files):
        if (!is_array($files)):
            $files = array($files);
        endif;
        foreach($files as $file):

?>
            <link rel="stylesheet" href="<?php echo $file;?>" type="text/css" />
<?php

        endforeach;
    endif;

    if($this->direction == 'rtl') : 

?>
    <link rel="stylesheet" href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template; ?>/css/template_rtl.css" type="text/css" />
<?php 
        
    endif; 

?>
    <!--[if lte IE 6]>
        <link href="<?php echo $this->baseurl ?>/templates/<?php echo $this->template; ?>/css/ieonly.css" rel="stylesheet" type="text/css" />
    <![endif]-->
</head>
<body class="contentpane">
    <div id="all">
        <div id="main">
            <!-- *********** debug:INCLUDE_START type="message"   in file: templates/breez_20/component.php -->
            <jdoc:include type="message"  />
            <!-- *********** debug:INCLUDE_END type="message"   in file: templates/breez_20/component.php -->

            <!-- *********** debug:INCLUDE_START type="component"   in file: templates/breez_20/component.php -->
            <jdoc:include type="component"  />
            <!-- *********** debug:INCLUDE_END type="component"   in file: templates/breez_20/component.php -->
        </div>
    </div>
</body>
</html>

<!-- *********** debug:END of file:   templates/beez_20/component.php    -->

