<!-- *********** debug:START of file:   templates/beez_20/html/modules.php   -->
<?php


/**
 * @package		Joomla.Site
 * @subpackage	Templates.beez_20
 * @copyright	Copyright (C) 2005 - 2012 Open Source Matters, Inc. All rights reserved.
 * @license		GNU General Public License version 2 or later; see LICENSE.txt
 */


// No direct access.
defined('_JEXEC') or die;




/**
 * beezDivision chrome.
 *
 * @since	1.6
 */
function modChrome_beezDivision($module, &$params, &$attribs)
{

?>
    <!-- *********** debug:entering modChrome_beezDivision() in file:   templates/beez_20/html/modules.php   -->
<?php

    $headerLevel = isset($attribs['headerLevel']) ? (int) $attribs['headerLevel'] : 3;
    if (!empty ($module->content)) { 

?>
    <div class="moduletable<?php echo htmlspecialchars($params->get('moduleclass_sfx')); ?>">
<?php 

        if ($module->showtitle) { 

?> 
        <h<?php echo $headerLevel; ?>>
            <span class="backh">
                <span class="backh2">
                    <span class="backh3">
                        <!-- *********** debug:ECHO module->title -->
<?php

            echo $module->title; 

?>

                    </span>
                </span>
            </span>
        </h<?php echo $headerLevel; ?>>
<?php

        }; 

?>
        <!-- *********** debug:ECHO_START module->content    -->
<?php

        echo $module->content; 

?>
        <!-- *********** debug:ECHO_END module->content    -->
    </div><!-- moduletable -->
<?php 
    
    };

?>
    <!-- *********** debug:exiting modChrome_beezDivision()  -->
<?php


} ////////////// end function modChrome_beezDivision ///////////////////







/**
 * beezHide chrome.
 *
 * @since	1.6
 */
function modChrome_beezHide($module, &$params, &$attribs)
{

?>
    <!-- *********** debug:entering modChrome_beezHide() in file:   templates/beez_20/html/modules.php   -->
<?php

    $headerLevel = isset($attribs['headerLevel']) ? (int) $attribs['headerLevel'] : 3;
    $state=isset($attribs['state']) ? (int) $attribs['state'] :0;

    if (!empty ($module->content)) { 

?>
    <div class="moduletable_js <?php echo htmlspecialchars($params->get('moduleclass_sfx'));?>">
<?php 

        if ($module->showtitle) : 

?>
        <h<?php echo $headerLevel; ?> class="js_heading">
            <span class="backh">
                <span class="backh1">
                    <!-- *********** debug:ECHO module->title  -->
<?php

            echo $module->title; 

?>

                    <a href="#" title="<?php echo JText::_('TPL_BEEZ2_CLICK'); ?>" 
                            onclick="auf('module_<?php echo $module->id; ?>'); return false"
                            class="opencloselink" id="link_<?php echo $module->id?>">
                         <span class="no">
                            <img src="templates/beez_20/images/plus.png" 
                                    alt="<?php if ($state == 1) { echo JText::_('TPL_BEEZ2_ALTOPEN');} 
                                               else {             echo JText::_('TPL_BEEZ2_ALTCLOSE');}     ?>" />
                        </span>
                    </a>
                </span>
            </span>
        </h<?php echo $headerLevel; ?>>
<?php
    
        endif; 

?>
        <div class="module_content <?php if ($state==1){echo "open";} ?>"  id="module_<?php echo $module->id; ?>" tabindex="-1">
            <!-- *********** debug:ECHO_START module->content  in file:   templates/beez_20/html/modules.php  -->
<?php
        echo $module->content; 

?>
            <!-- *********** debug:ECHO_END module->content    -->
        </div>
    </div>
<?php 

    }

?>
    <!-- *********** debug:exiting modChrome_beezHide()    -->
<?php

} ////////////// end function modChrome_beezHide ///////////////////






/**
 * beezTabs chrome.
 *
 * @since	1.6
 */
function modChrome_beezTabs($module, $params, $attribs)
{

?>
    <!-- *********** debug:entering modChrome_beezTabs() in file:   templates/beez_20/html/modules.php   -->
<?php

    $area = isset($attribs['id']) ? (int) $attribs['id'] :'1';
    $area = 'area-'.$area;

    static $modulecount;
    static $modules;

    if ($modulecount < 1) {
        $modulecount = count(JModuleHelper::getModules($attribs['name']));
        $modules = array();
    }

    if ($modulecount == 1) {
        $temp = new stdClass();
        $temp->content = $module->content;
        $temp->title = $module->title;
        $temp->params = $module->params;
        $temp->id=$module->id;
        $modules[] = $temp;

?>
    <div id="<?php echo $area; ?>" class="tabouter">
        <ul class="tabs">
<?php

        foreach($modules as $rendermodule) { ?>
            <li class="tab">
                <a href="#" id="link_<?php echo $rendermodule->id; ?>" class="linkopen" onclick="tabshow('module_<?php echo $rendermodule->id; ?>'); return false">
                    <!-- *********** debug:ECHO module->title    -->
<?php 

            echo $rendermodule->title;

?>
                </a>
            </li>
<?php

        }

?>
        </ul>
<?php

        $counter=0;

        // modulecontent
        foreach($modules as $rendermodule) {
            $counter ++;

?>
        <div tabindex="-1" class="tabcontent tabopen" id="module_<?php echo $rendermodule->id; ?>">
            <!-- *********** debug:ECHO_START module->content in file:   templates/beez_20/html/modules.php   -->
<?php
            echo $module->content;
?>
            <!-- *********** debug:ECHO_END module->content    -->
<?php
            if ($counter!= count($modules)) {

?>
            <a href="#" class="unseen" onclick="nexttab('module_<?php echo $rendermodule->id; ?>'); return false;" 
                    id="next_<?php echo $rendermodule->id; ?>">
<?php
    
            echo JText::_('TPL_BEEZ2_NEXTTAB');

?>
            </a>
<?php

            }

?>
        </div>
<?php

        }
        $modulecount--;

?>
    </div>
<?php

    } else {
        $temp = new stdClass();
        $temp->content = $module->content;
        $temp->params = $module->params;
        $temp->title = $module->title;
        $temp->id = $module->id;
        $modules[] = $temp;
        $modulecount--;
    }

?>
    <!-- *********** debug:exiting modChrome_beezTabs()    -->
<?php


} ////////////// end function modChrome_beezTabs ///////////////////



?>
<!-- *********** debug:END   of file:   templates/beez_20/html/modules.php   -->
<?php


